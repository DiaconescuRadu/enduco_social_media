package com.enduco.springboot.notion.socialmedia.scheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

	private static final String DISCORD_TOKEN = "MTA5MTMyODc1NDUwMjA3NDM4OQ.GF59mV.kn-VHerG8vVd_AhUJ5LeVyNV3uRMgZBWWyjWdI";
	private static final long CHANNEL_ID = 1091327560069156876L;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

//	@PostConstruct
//	public void onStartup() {
//		GatewayDiscordClient client = DiscordClientBuilder.create(DISCORD_TOKEN)
//				.build()
//				.login()
//				.block();
//
//		client.getChannelById(Snowflake.of(CHANNEL_ID))
//				.flatMap(channel -> ((TextChannel) channel).createMessage("Hello, Discord!"))
//				.block();
//
//		client.logout().block();
//	}
}