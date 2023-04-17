package com.enduco.springboot.notion.socialmedia.scheduler.notion;

import com.enduco.springboot.notion.socialmedia.scheduler.socialmedia.SocialMediaPost;
import com.iwebpp.crypto.TweetNaclFast;
import notion.api.v1.model.common.Cover;
import notion.api.v1.model.common.Icon;
import notion.api.v1.model.common.OptionColor;
import notion.api.v1.model.common.PropertyType;
import notion.api.v1.model.databases.DatabaseProperty;
import notion.api.v1.model.databases.QueryResults;
import notion.api.v1.model.databases.query.filter.QueryTopLevelFilter;
import notion.api.v1.model.databases.query.sort.QuerySort;
import notion.api.v1.model.pages.Page;
import notion.api.v1.model.pages.PageProperty;
import notion.api.v1.request.pages.UpdatePageRequest;
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

        QueryResults queryResult = client.queryDatabase("45b4172264ee4320b09336b285ab7521", filter, sorts, null, 100);


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

        QueryResults queryResult = client.queryDatabase("45b4172264ee4320b09336b285ab7521", filter, sorts, null, 100);


        socialMediaPosts = queryResult.getResults().stream()
                .map(this::transformPageToPost)
                .collect(Collectors.toList());

        Page pageToChange = queryResult.getResults().get(0);

        if (pageToChange.getProperties().containsKey("Status")) {
            PageProperty value = pageToChange.getProperties().get("Status");

            String postedOptionId = "76cdcc6e-ff77-4927-b659-6bc3dc86664f";
            String postedOptionText = "Posted";
            OptionColor postedOptionColor = OptionColor.Green;

            DatabaseProperty.Status.Option postedOption = new DatabaseProperty.Status.Option(postedOptionId, postedOptionText, postedOptionColor);

            value.setStatus(postedOption);
            Map<String, PageProperty> propertiesMap = new HashMap<>();

            propertiesMap.put("Status", value);

            client.updatePage(pageToChange.getId(), propertiesMap, null, null, null);
        } else {
            throw new IllegalArgumentException("Property not found in the page");
        }

        return true;
    }
}