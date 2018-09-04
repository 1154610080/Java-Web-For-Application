package com.web.site;

import com.web.site.entity.Discussion;
import com.web.site.entity.Reply;

import javax.inject.Inject;
import java.time.Instant;
import java.util.List;

/**
 * 默认回复服务类
 *
 * 实现获取一个讨论的所有回复和保存回复功能
 *
 * @author Egan
 * @date 2018/9/4 22:09
 **/
public class DefaultReplyService implements ReplyService {

    @Inject ReplyRepository replyRepository;
    @Inject DiscussionService discussionService;

    @Override
    public List<Reply> getRepliesForDiscussion(long discussionId) {
        List<Reply> list = this.replyRepository.getForDiscussion(discussionId);
        //使用lambada根据讨论Id由小到大进行排序
        list.sort((r1, r2) -> r1.getId() < r2.getId() ? -1 : 1);
        return list;
    }

    @Override
    public void saveReply(Reply reply) {
        Discussion discussion =
                this.discussionService.getDiscussion(reply.getDiscussionId());
        //如果回复不存在，创建回复；否则更新回复
        if(reply.getId() < 1){
            discussion.getSubScribedUsers().add(reply.getUser());
            reply.setCreated(Instant.now());
            this.replyRepository.add(reply);
        }else
            this.replyRepository.update(reply);
        this.discussionService.saveDiscussion(discussion);
    }
}
