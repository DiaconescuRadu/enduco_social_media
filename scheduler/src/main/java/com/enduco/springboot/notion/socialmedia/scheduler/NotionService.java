package com.enduco.springboot.notion.socialmedia.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class NotionService {

    //todo - get the api key from a properties file
    //@Value("${notion.api.key}")
    private String apiKey;

    @Autowired
    private RestTemplate restTemplate;

    public Object getDatabaseContents(String databaseId) {
        String url = "https://api.notion.com/v1/databases/" + "45b4172264ee4320b09336b285ab7521" + "/query";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Notion-Version", "2022-06-28"); // Update this with the latest version if required
        headers.set("Authorization", "Bearer " + "secret_C6vCjouKO6KzuhwRfu9iNaFkFkZiBoNN1Lxi9jNLRoK");

        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        return restTemplate.exchange(url, HttpMethod.POST, entity, Object.class).getBody();
    }

    public Object listDatabases() {
        String url = "https://api.notion.com/v1/databases";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Notion-Version", "2022-06-28"); // Update this with the latest version if required
        headers.set("Authorization", "Bearer " + "secret_C6vCjouKO6KzuhwRfu9iNaFkFkZiBoNN1Lxi9jNLRoK");

        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        return restTemplate.exchange(url, HttpMethod.GET, entity, Object.class).getBody();
    }
}