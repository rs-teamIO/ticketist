package com.siit.ticketist.repository;

import com.siit.ticketist.model.MediaFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MediaFileRepository extends JpaRepository<MediaFile, Long> {

    void deleteByFileName(String fileName);
}