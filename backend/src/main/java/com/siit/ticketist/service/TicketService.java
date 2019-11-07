package com.siit.ticketist.service;

import com.siit.ticketist.controller.exceptions.BadRequestException;
import com.siit.ticketist.controller.exceptions.NotFoundException;
import com.siit.ticketist.dto.TicketDTO;
import com.siit.ticketist.model.RegisteredUser;
import com.siit.ticketist.model.Ticket;
import com.siit.ticketist.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    public List<TicketDTO> findAllByEventId(Long id) {
        List<Ticket> tickets = ticketRepository.findTicketsByEventId(id);
        List<TicketDTO> ticketsDTO = new ArrayList<>();
        for (Ticket ticket : tickets) {
            ticketsDTO.add(new TicketDTO(ticket));
        }
        return ticketsDTO;
    }

    public List<TicketDTO> findAllByEventSectorId(Long id) {
        List<Ticket> tickets = ticketRepository.findTicketsByEventSectorId(id);
        List<TicketDTO> ticketsDTO = new ArrayList<>();
        for (Ticket ticket : tickets) {
            ticketsDTO.add(new TicketDTO(ticket));
        }
        return ticketsDTO;
    }

    private boolean checkTicketExistence(Long eventSectorId, Integer col, Integer row) {
        Optional<Ticket> ticket = ticketRepository.checkTicketExistence(eventSectorId, col, row);
        return ticket.isPresent();
    }

    public List<TicketDTO> makeReservations (List<TicketDTO> reservations) throws BadRequestException {
        boolean check = true;
        for(TicketDTO ticketDTO : reservations) {
            check = checkTicketExistence(ticketDTO.getEventSectorId(), ticketDTO.getNumberColumn(), ticketDTO.getNumberRow());
            if(check) break;
        }
        if(check) {
            throw new BadRequestException("Ticket is already taken");
        }

        RegisteredUser ru =(RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<TicketDTO> ticketDTOList = new ArrayList<>();

        for(TicketDTO ticketDTO : reservations) {
            ticketDTO.setUserId(ru.getId());
            ticketDTO.setIsPaid(false);
            ticketDTOList.add(new TicketDTO(ticketRepository.save(ticketDTO.convertToEntity())));
        }

        return ticketDTOList;
    }

}
