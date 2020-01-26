package com.siit.ticketist.service;

import com.siit.ticketist.exceptions.BadRequestException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;

/**
 * This class contains unit test methods for {@link QrCodeService}.
 */
public class QrCodeServiceUnitTest {

    private static final String CONTENT = "content";
    private static final int WIDTH = 200;
    private static final int HEIGHT = 200;
    private static final int INVALID_WIDTH = -1;
    private static final int INVALID_HEIGHT = -1;

    private static final String BAD_REQUEST_EXCEPTION_MESSAGE = "Error occurred while trying to generate QR Code.";

    @InjectMocks
    private QrCodeService qrCodeService;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    /**
     * Initializes mocks
     */
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Tests getQRCodeImage from {@link QrCodeService} when all input parameters are valid.
     * Should return a byte array.
     */
    @Test
    public void getQRCodeImage_shouldReturnByteArray_whenAllInputParametersAreValid() {

        // Act
        byte[] qrCode = this.qrCodeService.getQRCodeImage(CONTENT, WIDTH, HEIGHT);

        // Assert
        assertNotNull(qrCode);
        assertTrue(qrCode.length > 1);
    }

    /**
     * Tests getQRCodeImage from {@link QrCodeService} when given content to encode is invalid.
     * Should throw {@link BadRequestException}
     */
    @Test
    public void getQRCodeImage_shouldThrowBadRequestException_whenGivenContentIsInvalid() {

        // Arrange
        this.exceptionRule.expect(BadRequestException.class);
        this.exceptionRule.expectMessage(BAD_REQUEST_EXCEPTION_MESSAGE);

        // Act
        byte[] qrCode = this.qrCodeService.getQRCodeImage(null, WIDTH, HEIGHT);

        // Assert
        assertNull(qrCode);
    }

    /**
     * Tests getQRCodeImage from {@link QrCodeService} when given width of result image is invalid.
     * Should throw {@link BadRequestException}
     */
    @Test
    public void getQRCodeImage_shouldThrowBadRequestException_whenGivenWidthIsInvalid() {

        // Arrange
        this.exceptionRule.expect(BadRequestException.class);
        this.exceptionRule.expectMessage(BAD_REQUEST_EXCEPTION_MESSAGE);

        // Act
        byte[] qrCode = this.qrCodeService.getQRCodeImage(CONTENT, INVALID_WIDTH, HEIGHT);

        // Assert
        assertNull(qrCode);
    }

    /**
     * Tests getQRCodeImage from {@link QrCodeService} when given height of result image is invalid.
     * Should throw {@link BadRequestException}
     */
    @Test
    public void getQRCodeImage_shouldThrowBadRequestException_whenGivenHeightIsInvalid() {

        // Arrange
        this.exceptionRule.expect(BadRequestException.class);
        this.exceptionRule.expectMessage(BAD_REQUEST_EXCEPTION_MESSAGE);

        // Act
        byte[] qrCode = this.qrCodeService.getQRCodeImage(CONTENT, WIDTH, INVALID_HEIGHT);

        // Assert
        assertNull(qrCode);
    }
}
