## ElasticSearch

### 添加1条数据

```json
PUT /customer/_doc/1
{
  "name": "John Doe"
}
```

```json
POST /customer/_doc/2
{
  "name": "John Doe"
}
```

结果：

```json
{
  "_index" : "customer",    //索引名
  "_type" : "_doc",		   	//类型
  "_id" : "1",				//id
  "_version" : 1,			//版本  没修改一次就会+1
  "result" : "created",		//创建
  "_shards" : {				//分片
    "total" : 2,			//总共两个分片
    "successful" : 2,		//成功2个
    "failed" : 0			//失败0个
  },
  "_seq_no" : 26,
  "_primary_term" : 4
}
```

#### PUT 和 POST 的区别：

1.PUT 命令后边必须加id，不加就会报错。如果已经存在该id就是修改（"result" : "update"），没有就是创建（"result" : "created"）---- 幂等

2.POST 命令后边可以加id，也可以不加。如果已经存在该id就是修改（"result" : "update"），没有就是创建（"result" : "created"），不加id就产生随机值

### 查询一条数据

```json
GET /customer/_doc/1
```

结果：

```json
{
  "_index" : "customer",   //索引
  "_type" : "_doc",		   //类型
  "_id" : "1",             //id
  "_version" : 1,          //版本
  "_seq_no" : 26,
  "_primary_term" : 4,
  "found" : true,
  "_source" : {
    "name": "John Doe"    //数据
  }
}
```

### 查询全部并排序(默认只显示10条数据)

```json
GET /bank/_search
{
  "query": { "match_all": {} },
  "sort": [
    { "account_number": "asc" }
  ]
}
```

结果：

```json
{
  "took" : 23,                //花费时间
  "timed_out" : false,        //是否超时
  "_shards" : {
    "total" : 1,
    "successful" : 1,
    "skipped" : 0,
    "failed" : 0
  },
  "hits" : {                  //命中
    "total" : { 
      "value" : 1000,         //1000条
      "relation" : "eq"
    },
    "max_score" : null,        //最大得分
    "hits" : [   
      {
        "_index" : "bank",
        "_type" : "customer",
        "_id" : "0",
        "_score" : null,
        "_source" : {          //具体数据
          "account_number" : 0,
          "balance" : 16623,
          "firstname" : "Bradshaw",
          "lastname" : "Mckenzie",
          "age" : 29,
          "gender" : "F",
          "address" : "244 Columbus Place",
          "employer" : "Euron",
          "email" : "bradshawmckenzie@euron.com",
          "city" : "Hobucken",
          "state" : "CO"
        },
        "sort" : [              //排序号
          0
        ]
      },
      ......
      ......
      {
        "_index" : "bank",
        "_type" : "customer",
        "_id" : "9",
        "_score" : null,
        "_source" : {
          "account_number" : 9,
          "balance" : 24776,
          "firstname" : "Opal",
          "lastname" : "Meadows",
          "age" : 39,
          "gender" : "M",
          "address" : "963 Neptune Avenue",
          "employer" : "Cedward",
          "email" : "opalmeadows@cedward.com",
          "city" : "Olney",
          "state" : "OH"
        },
        "sort" : [
          9
        ]
      }
    ]
  }
}


```

### 指定查询范围

```json
GET /bank/_search
{
  "query": {
      "match_all": {} 
  },
  "sort": [
    { "account_number": "asc" }
  ],
  "from": 10,          //从第10条数据开始查询
  "size": 10		   //一共查询10条数据
}
```

### 指定查询条件

```json
 //查询 address 为 mill lane的数据  共有19条数据
GET /bank/_search
{
  "query": { 
      "match": { 
           "address": "mill lane"     
      } 
  }  
}
```

```json
//查询 address 为 mill lane的数据  共有1条数据
GET /bank/_search
{
  "query": { 
      "match_phrase": { 
          "address": "mill lane" 
      }
  }
}
```

match 中的查询条件会被分词

match_phrase 中查询的条件不会被分词（当成一个整体作为查询条件）

组合多个查询条件

```json
GET /bank/_search
{
  "query": {
    "bool": {
      "must": [
        {"match": {
          "age": "40"             //必须匹配 age 为40
        }}
      ],
      "must_not": [
        {"match": {
          "state": "ID"           //必须不匹配 state 为 ID
        }}
      ]
    }
  }
}
```

### 聚合查询（根据state分组并求人数）

```json
GET /bank/_search
{
  "size": 0,                            //响应仅包含聚合结果
  "aggs": {
    "group_by_state": {                 //聚合名 唯一即可
      "terms": {                        //汇总（类似于mysql中的分组后count）
        "field": "state.keyword"		//分组字段
      }
    }
  }
}
```

### 嵌套分组（根据state分组并求人数并求平均工资）

```json
GET /bank/_search
{
  "aggs": {
    "state_count": {
      "terms": {
        "field": "state.keyword"
      },
      "aggs": {
        "balance_avg": {
          "avg": {
            "field": "balance"
          }
        }
      }
    }
  }
}
```

### 聚合分组并排序(按照平均薪资降序--order只能用在terms的聚合里)

```json
GET /bank/_search
{
  "size": 0,    //响应仅包含聚合结果
  "aggs": {
    "group_by_state": {
      "terms": {
        "field": "state.keyword", 
        "order": {
          "average_balance": "desc"
        }
      },
      "aggs": {
        "average_balance": {
          "avg": {
            "field": "balance"
          }
        }
      }
    }
  }
}
```

### 复合查询

复合查询包括：bool 查询 、boosting 查询、constant_score 查询、dis_max 查询、function_score 查询

#### 1.bool 查询 

与文档匹配的查询，该文档与其他查询的布尔组合匹配。布尔查询映射到Lucene `BooleanQuery`。它是使用一个或多个布尔子句构建的，每个子句都具有类型的出现。

用于组合多个查询子句，作为默认查询 `must`，`should`，`must_not`，或filter。

filter等同于must但是不提供相关性得分（must_not也不提供相关性得分）

```json
POST _search
{
  "query": {
    "bool" : {
      "must" : {
        "term" : { "user.id" : "kimchy" }
      },
      "filter": {
        "term" : { "tags" : "production" }
      },
      "must_not" : {
        "range" : {
          "age" : { "gte" : 10, "lte" : 20 }
        }
      },
      "should" : [
        { "term" : { "tags" : "env1" } },
        { "term" : { "tags" : "deployed" } }
      ],
      "minimum_should_match" : 1,
      "boost" : 1.0
    }
  }
}
```



#### 2.boosting 查询

返回与`positive`查询匹配的文档，但减少与`negative`查询匹配的文档的分数。

```json
GET /_search
{
  "query": {
    "boosting": {
      "positive": {
        "term": {
          "text": "apple"
        }
      },
      "negative": {
        "term": {
          "text": "pie tart fruit crumble tree"
        }
      },
      "negative_boost": 0.5
    }
  }
}
```



#### 3.constant_score 查询

一个查询，它包装另一个查询，但在过滤器上下文中执行它。所有匹配的文档都被赋予相同的“常量” `_score`。

```json
GET /_search
{
  "query": {
    "constant_score": {
      "filter": {
        "term": { "user.id": "kimchy" }
      },
      "boost": 1.2
    }
  }
}
```



#### 4.dis_max 查询

一个查询，它接受多个查询，并返回与任何查询子句匹配的任何文档。当`bool`查询合并所有匹配查询的分数时，`dis_max`查询将使用单个最佳匹配查询子句的分数。

```json
GET /_search
{
  "query": {
    "dis_max": {
      "queries": [
        { "term": { "title": "Quick pets" } },
        { "term": { "body": "Quick pets" } }
      ],
      "tie_breaker": 0.7
    }
  }
}
```



#### 5.function_score 查询

使用功能修改主查询返回的分数，以考虑诸如流行度，新近度，距离或使用脚本实现的自定义算法等因素。

```json
GET /_search
{
  "query": {
    "function_score": {
      "query": { "match_all": {} },
      "boost": "5", 
      "functions": [
        {
          "filter": { "match": { "test": "bar" } },
          "random_score": {}, 
          "weight": 23
        },
        {
          "filter": { "match": { "test": "cat" } },
          "weight": 42
        }
      ],
      "max_boost": 42,
      "score_mode": "max",
      "boost_mode": "multiply",
      "min_score": 42
    }
  }
}
```

### sql查询

```json
POST /_sql?format=txt
{
  "query": "SELECT * FROM library WHERE release_date < '2000-01-01'"
}
```

结果：

```text
    author     |     name      |  page_count   |      release_date      
---------------+---------------+---------------+------------------------
Dan Simmons    |Hyperion       |482            |1989-05-26T00:00:00.000Z
Frank Herbert  |Dune           |604            |1965-06-01T00:00:00.000Z
```

### 元数据

| 元数据字段   | 注释                                           |
| :----------- | :--------------------------------------------- |
| _index       | 文档所属的索引                                 |
| _type        | 文档的映射类型                                 |
| _id          | 文件编号                                       |
| _source      | 表示文档正文的原始JSON                         |
| _size        | _source提供的字段大小（以字节为单位）          |
| _field_names | 文档中包含非空值的所有字段                     |
| _ignored     | 由于导致索引时间被忽略的文档中的所有字段       |
| _routing     | 一个自定义的路由值，用于将文档路由到特定的分片 |
| _meta        | 特定于应用程序的元数据                         |

### 映射参数

| 参数         | 注释                                                         |
| ------------ | ------------------------------------------------------------ |
| analyzer     | 指定索引或搜索字段时用于文本分析的分词器                     |
| boost        | 自动*增强*各个字段（对相关性得分进行更多计数）默认是1.0      |
| coerce       | 清除脏值以适合字段的数据类型                                 |
| copy_to      | 允许您将多个字段的值复制到组字段中，然后可以将其作为单个字段进行查询 |
| doc_values   | 设置字段是否可以排序或聚合                                   |
| dynamic      | 通过索引包含新字段的文档，即可将字段*动态*添加到文档或文档中的内部对象 |
| enabled      | 是否为字段建立索引                                           |
| format       | 格式化                                                       |
| ignore_above | 长于`ignore_above`设置的字符串将不会被索引或存储             |
| index        | 该`index`选项控制是否对字段值建立索引。它接受`true` 或`false`，默认为`true`。未索引的字段不可查询 |
| meta         | 附加到字段的元数据。该元数据对Elasticsearch不透明，仅对在相同索引上工作的多个应用程序共享有关字段（例如单位）的元信息有用 |

