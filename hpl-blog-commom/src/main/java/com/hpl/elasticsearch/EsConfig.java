package com.hpl.elasticsearch;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @author : rbe
 * @date : 2024/9/4 9:57
 */
@Configuration
public class EsConfig {

    @Value("${elasticsearch.address}")
    private String esAddress;

    @Bean
    public RestHighLevelClient restHighLevelClient() {
        return new RestHighLevelClient(RestClient.builder(
                HttpHost.create(esAddress)
        ));
    }
}
