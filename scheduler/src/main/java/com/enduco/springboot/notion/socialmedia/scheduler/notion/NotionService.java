package com.enduco.springboot.notion.socialmedia.scheduler.notion;

import com.enduco.springboot.notion.socialmedia.scheduler.socialmedia.SocialMediaPost;

import java.util.List;

public interface NotionService {
    List<SocialMediaPost> getSocialMediaPosts();
    boolean moveToPosted(SocialMediaPost post);

    boolean shouldPostBePosted(SocialMediaPost socialMediaPost);

    boolean isPostScheduled(SocialMediaPost post);
}
