package com.web.site;

import org.springframework.ws.server.endpoint.annotation.*;

import javax.inject.Inject;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;

/**
 * 票据Soap终端
 *
 *  该终端通过使用 @PayloadRoot 注解将载荷根元素匹配到终端操作方法上
 * 但也可以使用 @SoapAction 标注 SoapAction头，
 * 或者使用 Web Service Addressing 标准（www.w3.org/2005/08/addressing）
 * 的 @Action 注解。
 *
 * @author Egan
 * @date 2018/10/4 15:26
 **/
@Endpoint
public class TicketSoapEndpoint {

    private static final String NAMESPACE= "http://example.com/xmlns/support";

    @Inject TicketService ticketService;

    @PayloadRoot(namespace = NAMESPACE, localPart = "ticketsRequest")
    @ResponsePayload
    public TicketWebServiceList read(){
        TicketWebServiceList list = new TicketWebServiceList();
        list.setValue(this.ticketService.getAllTickets());
        return list;
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "ticketRequest")
    @Namespace(uri = NAMESPACE, prefix = "s")
    @ResponsePayload
    public Ticket read(@XPathParam("/s:ticketRequest/id") long id){
        return this.ticketService.getTicket(id);
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "createTicket")
    @ResponsePayload
    public Ticket create(@RequestPayload TicketForm form){
        Ticket ticket = new Ticket();
        ticket.setCustomerName("WebServiceAnonymous");
        ticket.setSubject(form.getSubject());
        ticket.setBody(form.getBody());

        this.ticketService.save(ticket);

        return ticket;
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "deleteTicket")
    @Namespace(uri = NAMESPACE, prefix = "s")
    public void delete(@XPathParam("/s:deleteTicket/id") long id){
        this.ticketService.delete(id);
    }

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
