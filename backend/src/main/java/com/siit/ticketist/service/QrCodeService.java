package com.siit.ticketist.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class QrCodeService {

    /*
    This method takes the text to be encoded, the width and height of the QR Code,
    and returns the QR Code in the form of a byte array.
    */
    private byte[] getQRCodeImage(String text, int width, int height) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);

            return outputStream.toByteArray();
        } catch (WriterException | IOException e) {
            e.printStackTrace();

            return null;
        }
    }
}
