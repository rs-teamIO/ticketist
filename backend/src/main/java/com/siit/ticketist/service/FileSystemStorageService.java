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

@Service
public class FileSystemStorageService implements StorageService {

    @Value("${storage-root}")
    private String root;

    @Override
    public void write(String fileName, MultipartFile file) {
        final String path = String.format("%s/%s", root, fileName);
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

    @Override
    public byte[] read(String fileName) {
        try {
            final File path = new File(String.format("%s/%s", root, fileName));
            return Files.readAllBytes(path.toPath());
        } catch (IOException e) {
            throw new NotFoundException("Requested media file not found.");
        }
    }
}
