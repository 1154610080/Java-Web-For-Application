package com.web.site;

import com.web.site.entity.Discussion;
import com.web.site.entity.Reply;
import com.web.site.form.ReplyForm;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.inject.Inject;
import java.util.Map;

/**
 * 讨论控制器
 *
 * 负责提供讨论视图和回复功能
 *
 * @author Egan
 * @date 2018/9/4 21:31
 **/
@Controller
@RequestMapping("discussion/{discussionId:\\d+}")
public class DiscussionController {

    @Inject
    DiscussionService discussionService;
    @Inject
    ReplyService replyService;

    @RequestMapping(value = {"", "list"}, method = RequestMethod.GET)
    public String viewDiscussion(Map<String, Object> model,
                                 @PathVariable("discussionId") long id){
        Discussion discussion = this.discussionService.getDiscussion(id);
        if(discussion != null){
            model.put("discussion", discussion);
            model.put("replies", this.replyService.getRepliesForDiscussion(id));
            model.put("replyForm", new ReplyForm());
            return "discussion/view";
        }

        return "discussion/errorNoDiscussion";
    }

    public ModelAndView reply(ReplyForm form,
                              @PathVariable("discussionId") long id){
        Discussion discussion = this.discussionService.getDiscussion(id);
        if(discussion != null){
            Reply reply = new Reply();
            reply.setDiscussionId(id);
            reply.setUser(form.getUser());
            reply.setMessage(form.getMessage());
            this.replyService.saveReply(reply);

            //刷新当前讨论页面
            return new ModelAndView(new RedirectView("/discussion/" + id + "/" +discussion.getUriSafeSubject(),
                    true, false));
        }

        return new ModelAndView(new RedirectView("/discussion/errorNoDiscussion"));
    }



}
