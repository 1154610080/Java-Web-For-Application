package com.web.site;

import com.web.site.entity.Discussion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.text.Normalizer;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * 默认讨论服务类
 *
 * 实现获取所有讨论、获取特定讨论、保存讨论的具体功能
 *
 * @author Egan
 * @date 2018/9/4 21:56
 **/
@Service
public class DefaultDiscussionService implements DiscussionService{

    private static Logger log = LogManager.getLogger();

    @Inject
    DiscussionRepository discussionRepository;

    @Override
    public List<Discussion> getAllDiscussions() {
        List<Discussion> list = this.discussionRepository.getAll();
        //使用lambada根据讨论的最后回复时间，由新到旧进行排序
        list.sort((d1, d2) -> d1.getLastUpdated().compareTo(d2.getLastUpdated()));
        return list;
    }

    @Override
    public Discussion getDiscussion(long id) {
        return this.discussionRepository.get(id);
    }

    @Override
    public void saveDiscussion(Discussion discussion) {
        String subject = discussion.getSubject();
        //保证主题uri安全
        subject = Normalizer.normalize(subject.toLowerCase(), Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .replaceAll("^\\p{Alnum}+", "-")
                .replace("--", "-").replace("--", "-")
                .replaceAll("[^a-z0-9]+$", "")
                .replaceAll("^[^a-z0-9]+", "");
        discussion.setUriSafeSubject(subject);

        Instant now = Instant.now();
        discussion.setLastUpdated(now);

        //如果讨论不存在，创建新讨论
        if(discussion.getId() < 1){
            discussion.setCreated(now);
            discussion.getSubScribedUsers().add(discussion.getUser());
            this.discussionRepository.add(discussion);
        }else   //否则更新
            this.discussionRepository.update(discussion);

    }

    @Scheduled(fixedDelay = 15_000L, initialDelay = 15_000L)
    public void deleteStaleDiscussions(){
        Instant oneYearAgo = Instant.now().minus(365L, ChronoUnit.DAYS);
        log.info("Deleting discussion stale since {}.", oneYearAgo);

        List<Discussion> list = this.discussionRepository.getAll();
        list.removeIf(d -> d.getLastUpdated().isAfter(oneYearAgo));

        for (Discussion old : list)
            this.discussionRepository.delete(old.getId());
    }
}
