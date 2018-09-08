package com.web.site;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import javax.servlet.http.HttpSession;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 票据控制器
 *
 * @author Egan
 * @date 2018/9/8 19:10
 **/
@Controller
@RequestMapping("ticket")
public class TicketController {

    private Logger log = LogManager.getLogger();

    private volatile long TICKET_ID_SEQUENCE = 1;

    private Map<Long, Ticket> ticketDatabase = new LinkedHashMap<>();

    /**
     * 列出所有票据
     *
     * @date 2018/9/8 19:29
     * @param model  通用模型
     * @return java.lang.String url
     **/
    @RequestMapping(value = {"", "list"}, method = RequestMethod.GET)
    public String list(Map<String, Object> model){
        log.debug("Listing tickets.");
        model.put("ticketDatabase", ticketDatabase);
        return "ticket/list";
    }

    /**
     * 查看特定票据
     *
     * @date 2018/9/8 19:30
     * @param model 通用模型
	 * @param ticketId 目标票据id
     * @return org.springframework.web.servlet.ModelAndView
     **/
    @RequestMapping(value = "view/{ticketId}", method = RequestMethod.GET)
    public ModelAndView view(Map<String, Object> model,
                             @PathVariable("ticketId") long ticketId){
        Ticket ticket = ticketDatabase.get(ticketId);
        if(ticket == null)
            return this.getListRedirectModelAndView();
        model.put("ticketId", Long.toString(ticketId));
        model.put("ticket", ticket);
        return new ModelAndView("ticket/view");
    }

    public View download(long ticketId, String name){}

    public String create(Map<String, Object> model){}

    public View create(HttpSession session, Form form){}

    private ModelAndView getListRedirectModelAndView(){}

    private View getListRedirectView(){}

    private synchronized long getNextTicketId(){return this.TICKET_ID_SEQUENCE++; }

    public static class Form{

        private String subject;

        private String body;

        private List<MultipartFile> attachments;

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }

        public List<MultipartFile> getAttachments() {
            return attachments;
        }

        public void setAttachments(List<MultipartFile> attachments) {
            this.attachments = attachments;
        }
    }
}
