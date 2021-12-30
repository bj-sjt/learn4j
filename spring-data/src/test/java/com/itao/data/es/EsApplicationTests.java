package com.itao.data.es;

import com.alibaba.fastjson.JSON;
import com.itao.data.es.bean.User;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.MaxAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.ParsedMax;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;

import java.io.IOException;
import java.util.List;

@SpringBootTest
class EsApplicationTests {

    @Autowired
    private ElasticsearchRestTemplate esTemplate;
    @Autowired
    private RestHighLevelClient client;

    @Test
    void index() throws IOException {

        IndexRequest indexRequest = new IndexRequest("person");
        indexRequest.id("1001");
        User user = new User("11111", 2, 2, "shang", "jiangtao", 18, "M", "hebei", "tkbs", "476004058@qq.com", "beijing", "NL");
        indexRequest.source(JSON.toJSONString(user), XContentType.JSON);
        client.index(indexRequest, RequestOptions.DEFAULT);
    }

    @Test
    void delete() throws IOException {
        DeleteRequest indexRequest = new DeleteRequest("person");
        indexRequest.id("1001");
        client.delete(indexRequest, RequestOptions.DEFAULT);
    }

    @Test
    void deleteIndex() throws IOException {
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest("person");
        client.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);
    }

    @Test
    void get() throws IOException {
        GetRequest getRequest = new GetRequest("user", "6");
        GetResponse response = client.get(getRequest, RequestOptions.DEFAULT);
        System.out.println(JSON.parseObject(response.getSourceAsString(), User.class));
    }

    @Test
    void search() throws IOException {
        SearchRequest searchRequest = new SearchRequest("user");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        QueryBuilder queryBuilder = QueryBuilders.matchQuery("address", "671 Bristol Street");
        searchSourceBuilder.query(queryBuilder);
        searchRequest.source(searchSourceBuilder);
        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits hits = response.getHits();
        for (SearchHit hit: hits) {
            System.out.println(hit.getSourceAsString());
        }
    }

    @Test
    void terms() throws IOException {
        SearchRequest searchRequest = new SearchRequest("user");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        TermsAggregationBuilder aggregationBuilder = AggregationBuilders.terms("ageTerms").field("age");
        searchSourceBuilder.aggregation(aggregationBuilder);
        searchSourceBuilder.size(0);
        searchRequest.source(searchSourceBuilder);
        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
        ParsedLongTerms ageAgg = response.getAggregations().get("ageTerms");
        List<? extends Terms.Bucket> buckets = ageAgg.getBuckets();
        for (Terms.Bucket bucket : buckets) {
            System.out.print(bucket.getKeyAsString() + "   ");
            System.out.println(bucket.getDocCount());
        }
    }

    @Test
    void max() throws IOException {
        SearchRequest searchRequest = new SearchRequest("user");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        MaxAggregationBuilder maxAggregationBuilder = AggregationBuilders.max("ageMax").field("age");
        searchSourceBuilder.aggregation(maxAggregationBuilder);
        searchSourceBuilder.size(0);
        searchRequest.source(searchSourceBuilder);
        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
        ParsedMax ageAgg = response.getAggregations().get("ageMax");
        System.out.println(ageAgg.getValue());

    }

}
