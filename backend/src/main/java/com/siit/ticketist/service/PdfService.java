package com.siit.ticketist.service;

import com.itextpdf.text.DocumentException;
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

    @Value("${templates.pdf.tickets}")
    private String ticketTemplateName;

    private final SpringTemplateEngine springTemplateEngine;

    @Autowired
    public PdfService(SpringTemplateEngine springTemplateEngine) {
        this.springTemplateEngine = springTemplateEngine;
    }

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
            // TODO: Handle exceptions
            e.printStackTrace();

            return null;
        }
    }

    byte[] generatePdfInvoice(List<PdfTicket> tickets) {
        Map<String, Object> templateData = new HashMap<>();
        templateData.put("tickets", tickets);

        return createPdf(ticketTemplateName, templateData);
    }
}
