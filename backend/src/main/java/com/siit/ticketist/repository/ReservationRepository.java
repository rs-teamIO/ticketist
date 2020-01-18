package com.siit.ticketist.repository;

import com.siit.ticketist.model.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    Page<Reservation> findAllByUserId(Long userId, Pageable pageable);
    Long countByUserId(Long userId);
}
