package com.web.site;

import com.web.site.entity.Discussion;
import com.web.site.entity.Reply;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * 内存回复仓库类
 *
 * 用于在内存中存储回复，
 * 实现了获取特定讨论的所有回复、添加回复、更新回复、生成新的回复序列号的具体功能
 *
 * @author Egan
 * @date 2018/9/4 22:25
 **/
@Repository
public class InMemoryReplyRepository implements ReplyRepository{

    private final Map<Long, Reply> database = new Hashtable<>();
    private volatile long replyIdSequence = 1L;

    @Override
    public List<Reply> getForDiscussion(long id) {
        ArrayList<Reply> list = new ArrayList<>(this.database.values());
        //排除不在本讨论的回复
        list.removeIf(r -> r.getDiscussionId() != id);
        return list;
    }

    @Override
    public void add(Reply reply) {
        reply.setId(getNextReplyId());
        this.database.put(reply.getId(), reply);
    }

    @Override
    public synchronized void update(Reply reply) {
        this.database.put(reply.getId(), reply);
    }

    @Override
    public void deleteForDiscussion(long id) {
        this.database.entrySet()
                .removeIf(e -> e.getValue().getId() == id);
    }

    private synchronized long getNextReplyId(){
        return this.replyIdSequence++;
    }
}
