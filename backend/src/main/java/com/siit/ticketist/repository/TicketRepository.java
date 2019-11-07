package com.siit.ticketist.repository;

import com.siit.ticketist.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    @Query(value = "select * from tickets t where t.event_id = ?1", nativeQuery = true)
    List<Ticket> findTicketsByEventId(Long id);

    @Query(value = "select * from tickets t where t.event_sector_id = ?1", nativeQuery = true)
    List<Ticket> findTicketsByEventSectorId(Long id);

    @Query(value = "select * from tickets t where t.event_sector_id = ?1 and t.number_column = ?2 and t.number_row =?3", nativeQuery = true)
    Optional<Ticket> checkTicketExistence(Long id, Integer col, Integer row);

    @Query(value =
            "SELECT u.email " +
            "FROM tickets t JOIN ticketist.users u " +
            "ON t.user_id = u.id " +
            "WHERE t.event_id = :eventId  AND t.is_paid = 0 " +
            "GROUP BY u.id", nativeQuery = true)
    List<String> findEmailsToBeNotified(Long eventId);

}
