package com.siit.ticketist.service.interfaces;

import org.springframework.web.multipart.MultipartFile;
/**
 * File storage service interface.
 */
public interface StorageService {

    /**
     * Write file to storage (filesystem, DB, etc.)
     *
     * @param id Id of the file
     * @param file Multipart File
     */
    void write(Long id, MultipartFile file);

    /**
     * Read file from storage (filesystem, DB, etc.)
     *
     * @param id Id of the file
     * @return Byte array file content
     */
    byte[] read(Long id);
}
