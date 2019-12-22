package com.siit.ticketist.service;

import com.siit.ticketist.exceptions.BadRequestException;
import com.siit.ticketist.exceptions.NotFoundException;
import com.siit.ticketist.service.interfaces.StorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileSystemStorageService implements StorageService {

    private static final String PATH_FORMAT = "%s/%s";

    @Value("${storage-root}")
    private String root;

    /**
     * Write file to filesystem storage
     *
     * @param fileName Name of the file
     * @param file Multipart File
     */
    @Override
    public void write(String fileName, MultipartFile file) {
        final String path = String.format(PATH_FORMAT, root, fileName);
        final File mediaFile = new File(path);
        try {
            if (mediaFile.exists()) {
                Files.delete(mediaFile.toPath());
            }
            Files.createFile(mediaFile.toPath());
        } catch (IOException e) {
            throw new BadRequestException("Error occurred while trying to create media file.");
        }

        try (final BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(mediaFile))) {
            final byte[] bytes = file.getBytes();
            stream.write(bytes);
        } catch (IOException e) {
            throw new BadRequestException("Error occurred while trying to save media file.");
        }
    }

    /**
     * Read file from filesystem storage
     *
     * @param fileName Name of the file
     * @return Byte array file content
     */
    @Override
    public byte[] read(String fileName) {
        try {
            final File path = new File(String.format(PATH_FORMAT, root, fileName));
            return Files.readAllBytes(path.toPath());
        } catch (IOException e) {
            throw new NotFoundException("Requested media file not found.");
        }
    }

    /**
     * Delete file from filesystem storage
     *
     * @param fileName Name of the file
     * @return true if operation was successful, otherwise false
     */
    @Override
    public boolean delete(String fileName) {
        final Path path = Paths.get(String.format(PATH_FORMAT, root, fileName));
        try{
            Files.delete(path);
            return true;
        } catch(Exception e) {
            throw new BadRequestException("Error occurred while trying to delete media file.");
        }
    }
}
