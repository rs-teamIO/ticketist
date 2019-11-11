package com.siit.ticketist.repository;

import com.siit.ticketist.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    @Query(value = "select * from tickets t where t.event_id = ?1", nativeQuery = true)
    List<Ticket> findTicketsByEventId(Long id);

    @Query(value = "select * from tickets t where t.event_sector_id = ?1", nativeQuery = true)
    List<Ticket> findTicketsByEventSectorId(Long id);

    @Query(value = "select * from tickets t where t.event_sector_id = ?1 and t.number_column = ?2 and t.number_row =?3", nativeQuery = true)
    Optional<Ticket> checkTicketExistence(Long id, Integer col, Integer row);

    @Query(value = "select * from tickets t where t.id in :ids and t.user_id = :userId and t.is_paid = 0", nativeQuery = true)
    List<Ticket> findTicketsByIdGroup(@Param("ids") List<Long> ticketIds, @Param("userId") Long userId);

    @Query(value = "select * from tickets t where t.user_id = ?1 and t.is_paid = 0", nativeQuery = true)
    List<Ticket> findUsersReservations(Long userId);

    @Query(value = "select * from tickets t where t.user_id = ?1 and t.is_paid = 1", nativeQuery = true)
    List<Ticket> findUsersTickets(Long userId);

    @Query(value =
            "SELECT u.email " +
                    "FROM tickets t JOIN users u " +
                    "ON t.user_id = u.id " +
                    "WHERE t.event_id = :eventId  AND t.is_paid = 0 " +
                    "GROUP BY u.id", nativeQuery = true)
    Set<String> findEmailsToBeNotified(Long eventId);
}
