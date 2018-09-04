package com.web.site;

import com.web.site.entity.Discussion;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * 内存讨论仓库类
 *
 * 用于在内存中存储讨论，
 * 实现获取所有讨论、获取特定讨论、添加讨论、更新讨论、生成讨论序列号的具体功能
 *
 * @author Egan
 * @date 2018/9/4 22:19
 **/
@Repository
public class InMemoryDiscussionRepository implements DiscussionRepository{

    private final Map<Long, Discussion> database = new Hashtable<>();
    private volatile long discussionIdSequence = 1L;

    @Override
    public List<Discussion> getAll() {
        return new ArrayList<>(this.database.values());
    }

    @Override
    public Discussion get(long id) {
        return this.database.get(id);
    }

    @Override
    public void add(Discussion discussion) {
        discussion.setId(this.getNextDiscussionId());
        this.database.put(discussion.getId(), discussion);
    }

    @Override
    public void update(Discussion discussion) {
        this.database.put(discussion.getId(), discussion);
    }

    private synchronized long getNextDiscussionId(){
        return this.discussionIdSequence++;
    }
}
