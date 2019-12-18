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

//    @Query(value = "select * from tickets t where t.event_id = ?1", nativeQuery = true)
    List<Ticket> findByEventId(Long id);

    @Query(value = "select * from tickets t where t.event_sector_id = ?1", nativeQuery = true)
    List<Ticket> findTicketsByEventSectorId(Long id);

    @Query(value = "select * from tickets t where t.user_id = ?1 and t.status = 0", nativeQuery = true)
    List<Ticket> findAllReservationsByUser(Long userId);

    @Query(value = "select * from tickets t where t.user_id = ?1 and t.event_id = ?2 and t.status = 0", nativeQuery = true)
    List<Ticket> findUsersReservationsByEvent(Long userId, Long eventID);

    @Query(value = "select * from tickets t where t.user_id = ?1 and t.status = 1", nativeQuery = true)
    List<Ticket> findUsersTickets(Long userId);

    @Query(value = "select * from tickets t where t.id in :ids and t.user_id = :userId and t.status = 0", nativeQuery = true)
    List<Ticket> findTicketsByIdGroup(@Param("ids") List<Long> ticketIds, @Param("userId") Long userId);

    /**
     * Retrieves a set of e-mails that should be notified about something.
     *
     * @param eventId ID of the event whose ticket users should be notified
     * @param ticketStatus {@link TicketStatus.RESERVED} used when only reservations are needed
     *                     {@link TicketStatus.PAID} used when both reservations and bought tickets are needed
     * @return List of strings containing emails
     */
    @Query(value =
            "SELECT u.email " +
            "FROM tickets t JOIN users u " +
            "ON t.user_id = u.id " +
            "WHERE t.event_id = :eventId AND (t.status = 1 OR t.status = :ticketStatus)" +
            "GROUP BY u.id", nativeQuery = true)
    Set<String> findEmailsToBeNotified(Long eventId, TicketStatus ticketStatus);

    @Modifying
    @Query("UPDATE Ticket t SET t.status = 0, t.user = null WHERE t.event = :event AND (t.status = 1 OR t.status = 2)")
    void deactivateTickets(@Param("event") Event event);


}
