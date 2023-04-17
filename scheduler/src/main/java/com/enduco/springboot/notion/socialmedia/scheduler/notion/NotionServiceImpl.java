package com.enduco.springboot.notion.socialmedia.scheduler.notion;

import com.enduco.springboot.notion.socialmedia.scheduler.socialmedia.SocialMediaPost;
import notion.api.v1.model.common.OptionColor;
import notion.api.v1.model.databases.DatabaseProperty;
import notion.api.v1.model.databases.QueryResults;
import notion.api.v1.model.databases.query.filter.QueryTopLevelFilter;
import notion.api.v1.model.databases.query.sort.QuerySort;
import notion.api.v1.model.pages.Page;
import notion.api.v1.model.pages.PageProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import notion.api.v1.NotionClient;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class NotionServiceImpl implements NotionService {

    @Value("${com.enduco.scheduler.query-interval}")
    private long queryInterval;

    @Value("${com.enduco.scheduler.notion.bearer-token}")
    private String token;

    @Value("${com.enduco.scheduler.notion.database-id}")
    private String databaseId;

    @Autowired
    private RestTemplate restTemplate;

    //TODO - move to injected / initialize in constructor
    NotionClient notionClient;
    private OffsetDateTime callTime;

    @Override
    public List<SocialMediaPost> getSocialMediaPosts() {
        List<SocialMediaPost> socialMediaPosts;
        NotionClient client = new NotionClient(token);

        QueryTopLevelFilter filter = new QueryTopLevelFilter() {
            @Override
            public int hashCode() {
                return super.hashCode();
            }
        };
        List<? extends QuerySort> sorts = new ArrayList<>();

        QueryResults queryResult = client.queryDatabase(databaseId, filter, sorts, null, 100);


        socialMediaPosts = queryResult.getResults().stream()
                .map(this::transformPageToPost)
                .collect(Collectors.toList());
        this.notionClient = client;
        this.callTime = OffsetDateTime.now();
        return socialMediaPosts;
    }

    private SocialMediaPost transformPageToPost(Page page) {
        PageProperty nameProperty = page.getProperties().entrySet().stream().filter(entry -> entry.getKey().contains("Name")).map(Map.Entry::getValue).findFirst().get();
        PageProperty statusProperty = page.getProperties().entrySet().stream().filter(entry -> entry.getKey().contains("Status")).map(Map.Entry::getValue).findFirst().get();
        PageProperty channelProperty = page.getProperties().entrySet().stream().filter(entry -> entry.getKey().contains("Channel")).map(Map.Entry::getValue).findFirst().get();
        PageProperty postAtProperty = page.getProperties().entrySet().stream().filter(entry -> entry.getKey().contains("Post at")).map(Map.Entry::getValue).findFirst().get();
        PageProperty contentProperty = page.getProperties().entrySet().stream().filter(entry -> entry.getKey().contains("Content")).map(Map.Entry::getValue).findFirst().get();

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

        return new SocialMediaPost(name, status, channel, postAt, content, page);
    }

    @Override
    public void moveToPosted(SocialMediaPost post) {
        Page pageToChange = post.getPage();
        if (pageToChange.getProperties().containsKey("Status")) {
            PageProperty value = pageToChange.getProperties().get("Status");

            String postedOptionId = "76cdcc6e-ff77-4927-b659-6bc3dc86664f";
            String postedOptionText = "Posted";
            OptionColor postedOptionColor = OptionColor.Green;

            DatabaseProperty.Status.Option postedOption = new DatabaseProperty.Status.Option(postedOptionId, postedOptionText, postedOptionColor);

            value.setStatus(postedOption);
            Map<String, PageProperty> propertiesMap = new HashMap<>();

            propertiesMap.put("Status", value);

            notionClient.updatePage(pageToChange.getId(), propertiesMap, null, null, null);
        } else {
            throw new IllegalArgumentException("Property not found in the page");
        }
    }

    @Override
    public boolean shouldPostBePosted(SocialMediaPost socialMediaPost) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        OffsetDateTime postDateTime = null;
        try {
            postDateTime = OffsetDateTime.parse(socialMediaPost.getPostAt(), formatter);
            System.out.println("OffsetDateTime: " + postDateTime);
        } catch (DateTimeParseException e) {
            System.err.println("Invalid date-time format: " + socialMediaPost.getPostAt());
        }

        Duration duration = Duration.between(callTime, postDateTime);
        return (duration.toMillis() > 0) && (duration.toMillis() < queryInterval);
    }

    @Override
    public boolean isPostScheduled(SocialMediaPost post) {
        return post.getStatus().equalsIgnoreCase("Scheduled");
    }

    @Override
    public void moveToFailure(SocialMediaPost post) {

    }
}