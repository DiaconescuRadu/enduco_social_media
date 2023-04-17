package com.enduco.springboot.notion.socialmedia.scheduler;

import com.enduco.springboot.notion.socialmedia.scheduler.notion.NotionService;
import com.enduco.springboot.notion.socialmedia.scheduler.socialmedia.SocialMediaPost;
import com.enduco.springboot.notion.socialmedia.scheduler.socialmedia.SocialMediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@EnableScheduling
@SpringBootApplication
public class Application {


	private static final String DISCORD_TOKEN = "MTA5MTMyODc1NDUwMjA3NDM4OQ.GF59mV.kn-VHerG8vVd_AhUJ5LeVyNV3uRMgZBWWyjWdI";
	private static final long CHANNEL_ID = 1091327560069156876L;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Autowired
	NotionService notionService;

	@Autowired
	SocialMediaService socialMediaService;

	@Scheduled(fixedRateString = "${com.enduco.scheduler.query-interval}")
	public void checkPosts() {
		List<SocialMediaPost> posts = notionService.getSocialMediaPosts();

		List<SocialMediaPost> postsToBePosted = posts.stream()
				.filter(post -> notionService.isPostScheduled(post))
				.filter(post -> notionService.shouldPostBePosted(post))
				.collect(Collectors.toList());

		postsToBePosted.stream()
				.forEach(post -> socialMediaService.post(post));

		postsToBePosted.stream()
				.forEach(post -> notionService.moveToPosted(post));

	}

}