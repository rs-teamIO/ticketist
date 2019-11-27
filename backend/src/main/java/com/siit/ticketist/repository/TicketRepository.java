package com.siit.ticketist.repository;

import com.siit.ticketist.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    @Lock(LockModeType.OPTIMISTIC)
    Optional<Ticket> findOneById(Long id);

    @Query(value = "select * from tickets t where t.event_id = ?1", nativeQuery = true)
    List<Ticket> findTicketsByEventId(Long id);

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
}
