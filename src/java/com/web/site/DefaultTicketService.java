package com.web.site;

import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.Instant;
import java.util.List;

/**
 * TicketService的实现类
 *
 * @author Egan
 * @date 2018/9/18 21:42
 **/
@Service
public class DefaultTicketService implements TicketService {

    @Inject TicketRepository ticketRepository;

    @Override
    public List<Ticket> getAllTickets() {
        return ticketRepository.getAll();
    }

    @Override
    public Ticket getTicket(long id) {
        return ticketRepository.get(id);
    }

    @Override
    public void save(Ticket ticket) {
        if(ticket.getId() < 1){
            ticket.setDateCreated(Instant.now());
            ticketRepository.add(ticket);
        }else
            ticketRepository.update(ticket);
    }
}
