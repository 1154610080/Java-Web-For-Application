package com.web.site.entity;

import java.time.Instant;

/**
 * 回复实体类
 *
 * @author Egan
 * @date 2018/9/4 19:47
 **/
public class Reply {

    //回复id
    private long id;
    //讨论id
    private long discussionId;
    //发表回复用户
    private String user;
    //回复内容
    private String message;
    //回复时间
    private Instant created;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getDiscussionId() {
        return discussionId;
    }

    public void setDiscussionId(long discussionId) {
        this.discussionId = discussionId;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Instant getCreated() {
        return created;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }
}
