package com.siit.ticketist.service;

import com.itextpdf.text.DocumentException;
import com.siit.ticketist.exceptions.BadRequestException;
import com.siit.ticketist.dto.PdfTicket;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * PDF Service implementation. Generates PDF files in-memory.
 */
@Service
public class PdfService {

    private static final String ENDPOINT = "https://localhost:8080/api/tickets/validate/";
    private static final Integer QR_CODE_WIDTH = 180;
    private static final Integer QR_CODE_HEIGHT = 180;

    @Value("${templates.pdf.tickets}")
    private String ticketTemplateName;

    private final SpringTemplateEngine springTemplateEngine;
    private final QrCodeService qrCodeService;

    @Autowired
    public PdfService(SpringTemplateEngine springTemplateEngine, QrCodeService qrCodeService) {
        this.springTemplateEngine = springTemplateEngine;
        this.qrCodeService = qrCodeService;
    }

    /**
     * Generates a PDF file based on given parameters
     * and returns it in the form of a byte array
     * @param templateName Name of the template file
     * @param variables Variables used for template data
     * @return byte array representation of the PDF file
     * @throws BadRequestException Exception thrown in case of an error
     */
    private byte[] createPdf(String templateName, Map<String, Object> variables) {
        Assert.notNull(templateName, "The templateName can not be null");
        String processedHtml = springTemplateEngine.process(templateName, new Context(Locale.getDefault(), variables));
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(processedHtml);
            renderer.layout();
            renderer.createPDF(outputStream, true);
            return outputStream.toByteArray();
        } catch (DocumentException | IOException e) {
            throw new BadRequestException("Error occurred while trying to create PDF.");
        }
    }

    /**
     * Generates a PDF invoice based on given parameters
     * and returns it in the form of a byte array
     * @param tickets List of PdfTicket objects to be included in the invoice
     * @return byte array representation of the invoice PDF file
     */
    byte[] generatePdfInvoice(List<PdfTicket> tickets) {
        Map<String, Object> templateData = new HashMap<>();
        tickets.forEach(pdfTicket -> {
            byte[] qrCodeImage = qrCodeService.getQRCodeImage(ENDPOINT.concat(pdfTicket.getTicketId()), QR_CODE_WIDTH, QR_CODE_HEIGHT);
            String base64 = Base64.encodeBase64String(qrCodeImage);
            String image = "data:image/jpg;base64,".concat(base64);
            pdfTicket.setBase64Image(image);
        });
        templateData.put("tickets", tickets);
        return createPdf(ticketTemplateName, templateData);
    }
}
