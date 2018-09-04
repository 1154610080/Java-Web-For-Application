package com.web.site;

import com.web.site.entity.Discussion;
import com.web.site.form.DiscussionForm;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

import javax.inject.Inject;
import java.util.Map;

/**
 * 板面控制器
 *
 * 负责列出、创建讨论
 *
 * @author Egan
 * @date 2018/9/4 19:32
 **/
@Controller
@RequestMapping("discussion")
public class BoardController {

    @Inject DiscussionService discussionService;

    @RequestMapping(value = {"", "list"}, method = RequestMethod.GET)
    public String listDiscussion(Map<String, Object> model){
        model.put("discussions", this.discussionService.getAllDiscussions());
        return "discussion/list";
    }

    @RequestMapping(value = "create", method = RequestMethod.GET)
    public String createDiscussion(Map<String, Object> model){
        model.put("discussionForm", new DiscussionForm());
        return "discussion/create";
    }

    @RequestMapping(value = "create", method = RequestMethod.POST)
    public View createDiscussion(DiscussionForm form){

        //保存讨论
        Discussion discussion = new Discussion();
        discussion.setUser(form.getUser());
        discussion.setSubject(form.getSubject());
        discussion.setMessage(form.getMessage());
        this.discussionService.saveDiscussion(discussion);

        //重定向至新创建的讨论
        return new RedirectView("/discussion/" + discussion.getId() + "/" +
            discussion.getUriSafeSubject(), true, false);
    }
}
