package com.yj.yjesjd.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticSearchConfig {
    @Bean//Spring <bean id="方法名" class="返回的类型">
    public RestHighLevelClient restHighLevelClient(){
        RestHighLevelClient restHighLevelClient = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost",9200,"http"))

        );
        return restHighLevelClient;
    }
}
