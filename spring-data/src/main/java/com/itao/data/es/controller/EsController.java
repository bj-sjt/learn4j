package com.itao.data.es.controller;


import com.itao.data.es.bean.User;
import lombok.AllArgsConstructor;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class EsController {

    private ElasticsearchRestTemplate esTemplate;
    private RestHighLevelClient client;

    @GetMapping("/es")
    public void test() {

        User u = new User();
        u.setId("6");
        Query query = new CriteriaQuery(Criteria.where("address").is("880 Holmes Lane"));
        SearchHits<User> hits = esTemplate.search(query, User.class);
        hits.forEach(System.out::println);
        System.out.println(client.indices());
    }
}
