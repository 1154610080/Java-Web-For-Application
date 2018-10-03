package com.web.site;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.validation.Valid;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlTransient;
import java.time.Instant;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 票据POJO
 *
 * @author Egan
 * @date 2018/9/8 19:03
 **/
public class Ticket {

    private long id;

    private String customerName;

    private String subject;

    private String body;

    Instant dateCreated;

    @Valid
    @XmlTransient
    private Map<String, Attachment> attachments = new LinkedHashMap<>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

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

    @XmlSchemaType(name = "dateTime")
    public Instant getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Instant dateCreated) {
        this.dateCreated = dateCreated;
    }

    @JsonIgnore
    public Attachment getAttachment(String name){
        return attachments.get(name);
    }

    @XmlElement(name = "attachment")
    public Collection<Attachment> getAttachments(){
        return attachments.values();
    }

    @JsonIgnore
    public void addAttachment(Attachment attachment){
        attachments.put(attachment.getName(), attachment);
    }

    @XmlTransient
    @JsonIgnore
    public int getNumberOfAttachments()
    {
        return this.attachments.size();
    }

    public void setAttachments(List<Attachment> attachments) {
        for (Attachment attachment : attachments)
            this.addAttachment(attachment);
    }
}
