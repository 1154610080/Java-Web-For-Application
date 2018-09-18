package com.web.site;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * TicketRepository的实现类
 *
 * @author Egan
 * @date 2018/9/18 21:32
 **/
@Repository
public class InMemoryTicketRepository implements TicketRepository {

    private volatile long TICKET_ID_SEQUENCE = 1L;

    private final Map<Long, Ticket> ticketDatabase = new Hashtable<>();

    @Override
    public List<Ticket> getAll() {
        return new ArrayList<>(ticketDatabase.values());
    }

    @Override
    public Ticket get(long id) {
        return this.ticketDatabase.get(id);
    }

    @Override
    public void add(Ticket ticket) {
        ticket.setId(getNextTicketId());
        ticketDatabase.put(ticket.getId(), ticket);
    }

    @Override
    public void update(Ticket ticket) {
        ticketDatabase.put(ticket.getId(), ticket);
    }

    private synchronized long getNextTicketId(){ return this.TICKET_ID_SEQUENCE++; }
}
