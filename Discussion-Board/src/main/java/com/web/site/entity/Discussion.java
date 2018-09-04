package com.web.site.entity;

import java.time.Instant;
import java.util.Set;

/**
 * 讨论实体类
 *
 * @author Egan
 * @date 2018/9/4 19:41
 **/
public class Discussion {

    /**
     * 讨论ID
     **/
    private long id;

    /**
     * 楼主
     **/
    private String user;

    /**
     * 主题
     **/
    private String subject;

    /**
     * 被转化为uri安全的主题
     **/
    private String uriSafeSubject;

    /**
     * 讨论内容
     **/
    private String message;

    /**
     * 创建日期
     **/
    private Instant created;

    /**
     * 最后发帖时间
     **/
    private Instant lastUpdated;

    /**
     * 所有参与讨论的用户
     **/
    private Set<String> subScribedUsers;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getUriSafeSubject() {
        return uriSafeSubject;
    }

    public void setUriSafeSubject(String uriSafeSubject) {
        this.uriSafeSubject = uriSafeSubject;
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

    public Instant getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Instant lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Set<String> getSubScribedUsers() {
        return subScribedUsers;
    }

    public void setSubScribedUsers(Set<String> subScribedUsers) {
        this.subScribedUsers = subScribedUsers;
    }
}
