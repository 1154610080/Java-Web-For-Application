package com.web.site;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * 票据服务列表
 *  负责将Json响应转换为XML
 *
 * @author Egan
 * @date 2018/10/3 16:48
 **/
@XmlRootElement(name = "tickets")
public class TicketWebServiceList {

    private List<Ticket> value;

    @XmlElement(name = "ticket")
    public List<Ticket> getValue(){
        return value;
    }

    public void setValue(List<Ticket> value) {
        this.value = value;
    }
}
