package com.web.site;

import com.web.config.annotation.RestEndPoint;
import com.web.exception.ResourceNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.inject.Inject;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * 票据RESTful 服务请求终端
 *
 * @author Egan
 * @date 2018/10/3 16:36
 **/
@RestEndPoint
public class TicketRestEndpoint {

    @Inject TicketService ticketService;

    /**
     * 发型机制
     *  通知客户端集合可用的选项
     *
     * @date 2018/10/3 16:39
     * @param
     * @return org.springframework.http.ResponseEntity<java.lang.Void>
     **/
    @RequestMapping(value = "ticket", method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> discover() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Allow", "OPTIONS,HEAD,GET,POST");
        return new ResponseEntity<>(null, headers, HttpStatus.NO_CONTENT);
    }

    /**
     * 发现机制
     *  通知客户端单个资源可用的选项
     *
     * @date 2018/10/3 16:41  
     * @param id 票据ID
     * @return org.springframework.http.ResponseEntity<java.lang.Void> 
     **/  
    @RequestMapping(value = "ticket/{id}", method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> discover(@PathVariable("id") long id){
        if(this.ticketService.getTicket(id) == null)
            throw new ResourceNotFoundException();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Allow", "OPTIONS,HEAD,GET,PUT,DELETE");
        return new ResponseEntity<>(null, headers, HttpStatus.NO_CONTENT);
    }

    /**
     * 获取集合
     *
     * @date 2018/10/3 16:55
     * @param
     * @return com.web.site.TicketWebServiceList 所有票据
     **/
    @RequestMapping(value = "ticket", method = RequestMethod.GET)
    @ResponseBody @ResponseStatus(HttpStatus.OK)
    public TicketWebServiceList read(){
        TicketWebServiceList list = new TicketWebServiceList();
        list.setValue(this.ticketService.getAllTickets());
        return list;
    }

    /**
     * 获取单个资源
     *
     * @date 2018/10/3 16:55
     * @param id   票据ID
     * @return com.web.site.Ticket 目标票据
     **/
    @RequestMapping(value = "ticket/{id}", method = RequestMethod.GET)
    @ResponseBody @ResponseStatus(HttpStatus.OK)
    public Ticket read(@PathVariable("id") long id){

        Ticket ticket = ticketService.getTicket(id);
        if(ticket == null)
            throw new ResourceNotFoundException();
        return ticket;
    }

    /**
     * 创建票据
     *
     * @date 2018/10/3 19:06
     * @param form 提交的票据表单
     * @return org.springframework.http.ResponseEntity<com.web.site.Ticket>
     **/
    @RequestMapping(value = "ticket", method = RequestMethod.POST)
    public ResponseEntity<Ticket> create(@RequestBody TicketForm form){
        Ticket ticket = new Ticket();
        ticket.setCustomerName("WebServiceAnonymous");
        ticket.setSubject(form.getSubject());
        ticket.setBody(form.getBody());
        ticket.setAttachments(form.getAttachments());

        this.ticketService.save(ticket);

        String uri = ServletUriComponentsBuilder.fromCurrentServletMapping()
                .path("/ticket/{id}").buildAndExpand(ticket.getId()).toString();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", uri);

        return new ResponseEntity<>(ticket, headers, HttpStatus.CREATED);

    }

    @RequestMapping(value = "ticket/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") long id){
        Ticket ticket = this.ticketService.getTicket(id);
        if(ticket == null)
            throw new ResourceNotFoundException();
        this.ticketService.delete(id);
    }

    @XmlRootElement(name = "ticket")
    public static class TicketForm{
        private String subject;
        private String body;
        private List<Attachment> attachments;

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

        @XmlElement(name = "attachment")
        public List<Attachment> getAttachments() {
            return attachments;
        }

        public void setAttachments(List<Attachment> attachments) {
            this.attachments = attachments;
        }
    }
}
