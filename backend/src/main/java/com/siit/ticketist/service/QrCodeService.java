package com.siit.ticketist.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.siit.ticketist.controller.exceptions.BadRequestException;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * QR Code Service implementation. Generates QR Code images in-memory.
 */
@Service
public class QrCodeService {

    /**
     * Generates a QR code image based on given parameters
     * and returns it in the form of a byte array
     * @param content Text to be encoded into the QR Code
     * @param width Width of the generated image
     * @param height Height of the generated image
     * @return byte array representation of the QR Code image
     * @throws BadRequestException thrown in case of an error
     */
    public byte[] getQRCodeImage(String content, int width, int height) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height);
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            return outputStream.toByteArray();
        } catch (WriterException | IOException e) {
            throw new BadRequestException("Error occurred while trying to generate QR Code.");
        }
    }
}
