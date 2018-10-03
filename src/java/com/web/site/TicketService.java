package com.web.site;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

public interface TicketService {
    @NotNull
    List<Ticket> getAllTickets();
    Ticket getTicket(@Min(value = 1L, message = "{validate.TicketService.getTicket.id}")
                             long id);
    void save(@NotNull(message = "{validate.TicketService.save.ticket}") @Valid Ticket ticket);
    void delete(long id);
}
