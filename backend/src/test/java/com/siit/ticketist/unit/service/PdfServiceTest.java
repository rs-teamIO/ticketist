package com.siit.ticketist.unit.service;

import com.siit.ticketist.dto.PdfTicket;
import com.siit.ticketist.exceptions.BadRequestException;
import com.siit.ticketist.service.PdfService;
import com.siit.ticketist.service.QrCodeService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * This class contains unit test methods for {@link PdfService}.
 */
public class PdfServiceTest {

    private static final String ENDPOINT = "https://localhost:8080/api/tickets/validate/";
    private static final Integer QR_CODE_WIDTH = 180;
    private static final Integer QR_CODE_HEIGHT = 180;

    private static final String VALID_CONTENT = "<html> <head> <meta charset=\"UTF-8\"></meta> <title>Title Goes Here</title> </head> <body> <p>This is my web page</p> </body> </html>";
    private static final String INVALID_CONTENT = "invalid_content";
    private static final String TICKET_TEMPLATE_NAME = "ticketsPDF";
    private static final String TICKET_ID = "1";

    @Mock
    private ITemplateEngine springTemplateEngineMock;

    @Mock
    private QrCodeService qrCodeServiceMock;

    @InjectMocks
    private PdfService pdfService;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    /**
     * Initializes mocks
     */
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        ReflectionTestUtils.setField(pdfService, "ticketTemplateName", TICKET_TEMPLATE_NAME, String.class);
    }

    /**
     * Tests generatePdfInvoice when HTML template is valid.
     */
    @Test
    public void generatePdfInvoice_shouldGeneratePdfInvoice_whenGivenHTMLIsValid() {
        // Arrange
        List<PdfTicket> tickets = new ArrayList<>();
        PdfTicket pdfTicket = new PdfTicket(TICKET_ID, "", "", "", "", "", "", "", "");
        tickets.add(pdfTicket);

        when(this.springTemplateEngineMock.process(eq(TICKET_TEMPLATE_NAME), any(Context.class)))
                .thenReturn(VALID_CONTENT);
        when(this.qrCodeServiceMock.getQRCodeImage(ENDPOINT.concat(TICKET_ID), QR_CODE_WIDTH, QR_CODE_HEIGHT))
                .thenReturn(VALID_CONTENT.getBytes(StandardCharsets.UTF_8));

        // Act
        byte[] byteArray = this.pdfService.generatePdfInvoice(tickets);

        // Assert
        verify(this.springTemplateEngineMock, times(1)).process(eq(TICKET_TEMPLATE_NAME), any(Context.class));
        verify(this.qrCodeServiceMock, times(1)).getQRCodeImage(ENDPOINT.concat(TICKET_ID), QR_CODE_WIDTH, QR_CODE_HEIGHT);
        assertNotNull(byteArray);
        assertThat(VALID_CONTENT.getBytes(StandardCharsets.UTF_8).length, greaterThan(1));
    }

    /**
     * Tests generatePdfInvoice when HTML template is invalid.
     * Should throw {@link BadRequestException}.
     */
    @Test
    public void generatePdfInvoice_shouldThrowBadRequestException_whenGivenHTMLIsInvalid() {
        // Arrange
        exceptionRule.expect(BadRequestException.class);
        List<PdfTicket> tickets = new ArrayList<>();
        PdfTicket pdfTicket = new PdfTicket(TICKET_ID, "", "", "", "", "", "", "", "");
        tickets.add(pdfTicket);

        when(this.springTemplateEngineMock.process(eq(TICKET_TEMPLATE_NAME), any(Context.class)))
                .thenReturn(INVALID_CONTENT);

        when(qrCodeServiceMock.getQRCodeImage(ENDPOINT.concat(TICKET_ID), QR_CODE_WIDTH, QR_CODE_HEIGHT))
                .thenReturn(INVALID_CONTENT.getBytes(StandardCharsets.UTF_8));

        // Act
        this.pdfService.generatePdfInvoice(tickets);
    }

    /**
     * Tests generatePdfInvoice when ticket list is empty.
     */
    @Test
    public void generatePdfInvoice_shouldGenerateEmptyPdfInvoice_whenTicketListIsEmpty() {

        // Arrange
        when(this.springTemplateEngineMock.process(eq(TICKET_TEMPLATE_NAME), any(Context.class)))
                .thenReturn(VALID_CONTENT);
        when(this.qrCodeServiceMock.getQRCodeImage(ENDPOINT.concat(TICKET_ID), QR_CODE_WIDTH, QR_CODE_HEIGHT))
                .thenReturn(VALID_CONTENT.getBytes(StandardCharsets.UTF_8));
        // Act
        byte[] byteArray = this.pdfService.generatePdfInvoice(new ArrayList<>());

        // Assert
        verify(this.springTemplateEngineMock, times(1)).process(eq(TICKET_TEMPLATE_NAME), any(Context.class));
        verify(this.qrCodeServiceMock, times(0)).getQRCodeImage(ENDPOINT, QR_CODE_WIDTH, QR_CODE_HEIGHT);
        assertNotNull(byteArray);
        assertThat(VALID_CONTENT.getBytes(StandardCharsets.UTF_8).length, greaterThan(1));
    }
}
