package com.siit.ticketist.integration.service;

import com.siit.ticketist.dto.PdfTicket;
import com.siit.ticketist.service.PdfService;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;

/**
 * This class contains integration test methods for {@link PdfService}.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PdfServiceTest {

    private static final String TICKET_ID = "1";

    @Autowired
    private PdfService pdfService;

    /**
     * Tests generatePdfInvoice when HTML template is valid.
     */
    @Test
    public void generatePdfInvoice_shouldGeneratePdfInvoice_whenGivenHTMLIsValid() {
        // Arrange
        List<PdfTicket> tickets = new ArrayList<>();
        PdfTicket pdfTicket = new PdfTicket(TICKET_ID, "", "", "", "", "", "", "", "");
        tickets.add(pdfTicket);

        // Act
        byte[] byteArray = this.pdfService.generatePdfInvoice(tickets);
        assertNotNull(byteArray);
    }

    /**
     * Tests generatePdfInvoice when ticket list is empty.
     */
    @Test
    public void generatePdfInvoice_shouldGenerateEmptyPdfInvoice_whenTicketListIsEmpty() {
        // Act
        byte[] byteArray = this.pdfService.generatePdfInvoice(new ArrayList<>());
        assertNotNull(byteArray);
    }
}
