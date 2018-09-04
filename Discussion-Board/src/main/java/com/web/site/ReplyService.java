package com.web.site;

import com.web.site.entity.Reply;

import java.util.List;

public interface ReplyService {
    List<Reply> getRepliesForDiscussion(long discussionId);
    void saveReply(Reply reply);
}
