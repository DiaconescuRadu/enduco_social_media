package com.enduco.springboot.notion.socialmedia.scheduler.notion;

import com.enduco.springboot.notion.socialmedia.scheduler.socialmedia.SocialMediaPost;
import notion.api.v1.model.databases.QueryResults;
import notion.api.v1.model.databases.query.filter.QueryTopLevelFilter;
import notion.api.v1.model.databases.query.sort.QuerySort;
import notion.api.v1.model.pages.Page;
import notion.api.v1.model.pages.PageProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import notion.api.v1.NotionClient;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class NotionServiceImpl implements NotionService {

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

    /*public Object getDatabaseContentsNotion(String databaseId) {
        SearchResults results = null;
        NotionClient client = new NotionClient("secret_C6vCjouKO6KzuhwRfu9iNaFkFkZiBoNN1Lxi9jNLRoK");

        QueryTopLevelFilter filter = new QueryTopLevelFilter() {
            @Override
            public int hashCode() {
                return super.hashCode();
            }
        };
        List<? extends QuerySort> sorts = new ArrayList<>();
        String startCursor = "0";
        String pageSize = "10";

        QueryResults queryResult = client.queryDatabase("45b4172264ee4320b09336b285ab7521", filter, sorts, null, 10);


        //pages seem to be the equivalent of rows in notion, transforming the rows into SocialMediaPost objects

        Page lastPage = queryResult.getResults().get(3);

        PageProperty nameProperty = lastPage.getProperties().entrySet().stream().filter(entry -> entry.getKey().contains("Name")).map(entry -> entry.getValue()).findFirst().get();
        PageProperty statusProperty = lastPage.getProperties().entrySet().stream().filter(entry -> entry.getKey().contains("Status")).map(entry -> entry.getValue()).findFirst().get();
        PageProperty channelProperty = lastPage.getProperties().entrySet().stream().filter(entry -> entry.getKey().contains("Channel")).map(entry -> entry.getValue()).findFirst().get();
        PageProperty postAtProperty = lastPage.getProperties().entrySet().stream().filter(entry -> entry.getKey().contains("Post at")).map(entry -> entry.getValue()).findFirst().get();
        PageProperty contentProperty = lastPage.getProperties().entrySet().stream().filter(entry -> entry.getKey().contains("Content")).map(entry -> entry.getValue()).findFirst().get();

        String name = nameProperty.getTitle().stream()
                .map(notion.api.v1.model.pages.PageProperty.RichText::getPlainText)
                .collect(Collectors.joining());

        String status = statusProperty.getStatus().getName();

        String channel = channelProperty.getMultiSelect().get(0).getName();

        String postAt = postAtProperty.getDate().getStart();

        String content = contentProperty.getRichText().stream()
                .map(notion.api.v1.model.pages.PageProperty.RichText::getPlainText)
                .collect(Collectors.joining());

        SocialMediaPost post = new SocialMediaPost(name, status, channel, postAt, content);



        }*/



    public Object listDatabases() {
        String url = "https://api.notion.com/v1/databases";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Notion-Version", "2022-06-28"); // Update this with the latest version if required
        headers.set("Authorization", "Bearer " + "secret_C6vCjouKO6KzuhwRfu9iNaFkFkZiBoNN1Lxi9jNLRoK");

        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        return restTemplate.exchange(url, HttpMethod.GET, entity, Object.class).getBody();
    }

    @Override
    public List<SocialMediaPost> getSocialMediaPosts() {
        List<SocialMediaPost> socialMediaPosts;
        NotionClient client = new NotionClient("secret_C6vCjouKO6KzuhwRfu9iNaFkFkZiBoNN1Lxi9jNLRoK");

        QueryTopLevelFilter filter = new QueryTopLevelFilter() {
            @Override
            public int hashCode() {
                return super.hashCode();
            }
        };
        List<? extends QuerySort> sorts = new ArrayList<>();
        String startCursor = "0";
        String pageSize = "10";

        QueryResults queryResult = client.queryDatabase("45b4172264ee4320b09336b285ab7521", filter, sorts, null, 10);


        //pages seem to be the equivalent of rows in notion, transforming the rows into SocialMediaPost objects

/*
        Page lastPage = queryResult.getResults().get(3);

        transformPageToPost(lastPage);
*/

        socialMediaPosts = queryResult.getResults().stream()
                .map(this::transformPageToPost)
                .collect(Collectors.toList());

        return socialMediaPosts;
    }

    private SocialMediaPost transformPageToPost(Page lastPage) {
        PageProperty nameProperty = lastPage.getProperties().entrySet().stream().filter(entry -> entry.getKey().contains("Name")).map(entry -> entry.getValue()).findFirst().get();
        PageProperty statusProperty = lastPage.getProperties().entrySet().stream().filter(entry -> entry.getKey().contains("Status")).map(entry -> entry.getValue()).findFirst().get();
        PageProperty channelProperty = lastPage.getProperties().entrySet().stream().filter(entry -> entry.getKey().contains("Channel")).map(entry -> entry.getValue()).findFirst().get();
        PageProperty postAtProperty = lastPage.getProperties().entrySet().stream().filter(entry -> entry.getKey().contains("Post at")).map(entry -> entry.getValue()).findFirst().get();
        PageProperty contentProperty = lastPage.getProperties().entrySet().stream().filter(entry -> entry.getKey().contains("Content")).map(entry -> entry.getValue()).findFirst().get();

        String name = nameProperty.getTitle().stream()
                .map(PageProperty.RichText::getPlainText)
                .collect(Collectors.joining());

        String status = statusProperty.getStatus().getName();

        String channel = "";
        if (!channelProperty.getMultiSelect().isEmpty())
            channel = channelProperty.getMultiSelect().get(0).getName();

        String postAt = "";
        if (postAtProperty.getDate() != null) {
            postAt = postAtProperty.getDate().getStart();
        }

        String content = contentProperty.getRichText().stream()
                .map(PageProperty.RichText::getPlainText)
                .collect(Collectors.joining());

        return new SocialMediaPost(name, status, channel, postAt, content);
    }

    @Override
    public boolean moveToPosted(SocialMediaPost post) {
        return false;
    }
}