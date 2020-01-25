package com.siit.ticketist.repository;

import com.siit.ticketist.model.Event;
import com.siit.ticketist.model.Ticket;
import com.siit.ticketist.model.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    @Lock(LockModeType.OPTIMISTIC)
    Optional<Ticket> findOneById(Long id);

    List<Ticket> findByEventId(Long id);

    List<Ticket> findByEventSectorId(Long id);

    List<Ticket> findAllByUserIdAndStatusAndEventId(Long userId, TicketStatus ticketStatus, Long eventId);

    List<Ticket> findAllByUserIdAndStatus(Long userId, TicketStatus ticketStatus);

    /**
     * Retrieves a set of e-mails that should be notified about:
     *  - reservation about to expire
     *  - event cancelled
     *
     * @param eventId ID of the event whose ticket users should be notified
     * @param ticketStatus {@link TicketStatus.RESERVED} used when only reservations are needed
     *                     {@link TicketStatus.PAID} used when both reservations and bought tickets are needed
     * @return Set of strings containing emails
     */
    @Query(value =
            "SELECT u.email " +
            "FROM tickets t JOIN users u " +
            "ON t.user_id = u.id " +
            "WHERE t.event_id = :eventId AND (t.status = 1 OR t.status = :ticketStatus)" +
            "GROUP BY u.id", nativeQuery = true)
    Set<String> findEmailsToBeNotified(Long eventId, Integer ticketStatus);

    /**
     * Deactivates tickets by setting their status to {@link TicketStatus.EVENT_CANCELLED}
     *
     * @param event {@link Event} Cancelled event whose tickets are to be deactivated
     */
    @Modifying
    @Query("UPDATE Ticket t SET t.status = 4 WHERE t.event = :event")
    void deactivateTickets(@Param("event") Event event);

}
