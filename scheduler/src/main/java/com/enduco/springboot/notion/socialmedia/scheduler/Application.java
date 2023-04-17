package com.enduco.springboot.notion.socialmedia.scheduler;

import com.enduco.springboot.notion.socialmedia.scheduler.notion.NotionService;
import com.enduco.springboot.notion.socialmedia.scheduler.socialmedia.SocialMediaPost;
import com.enduco.springboot.notion.socialmedia.scheduler.socialmedia.SocialMediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

@EnableScheduling
@SpringBootApplication
public class Application {

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
				.toList();

		postsToBePosted
				.forEach(this::handlePost);
	}

	public void handlePost(SocialMediaPost post) {
		if (socialMediaService.post(post)) {
			notionService.moveToPosted(post);
		} else {
			notionService.moveToFailure(post);
		}
	}

}