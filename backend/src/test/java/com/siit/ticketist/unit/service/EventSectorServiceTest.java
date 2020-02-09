package com.siit.ticketist.unit.service;

import com.siit.ticketist.exceptions.BadRequestException;
import com.siit.ticketist.model.*;
import com.siit.ticketist.repository.EventSectorRepository;
import com.siit.ticketist.service.EventSectorService;
import com.siit.ticketist.service.EventService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * This class contains unit test methods for {@link EventSectorService}.
 */
public class EventSectorServiceTest {

    private static final Long EVENT_ID = 1L;
    private static final Long EVENT_SECTOR_ID = 1L;
    private static final Long EVENT_SECTOR_ID_2 = 2L;
    private static final BigDecimal OLD_TICKET_PRICE = BigDecimal.valueOf(1);
    private static final BigDecimal NEW_TICKET_PRICE = BigDecimal.valueOf(100);
    private static final BigDecimal INVALID_TICKET_PRICE = BigDecimal.valueOf(-1);
    private static final String EVENT_EXCEPTION_MESSAGE = "Event doesn't contain sector with given ID.";
    private static final String EVENT_SECTOR_EXCEPTION_MESSAGE = "Event sector with given ID not found.";
    private static final String TICKET_PRICE_EXCEPTION_MESSAGE = "Ticket price invalid.";
    private static final String UPDATE_STATUS_EXCEPTION_MESSAGE = "This event sector can't be disabled as there are purchased tickets.";
    private static final String INVALID_CAPACITY_EXCEPTION_MESSAGE = "Invalid capacity.";
    private static final String NUMERATED_SEATS_EXCEPTION_MESSAGE = "Can't change capacity. Seats are numerated.";
    private static final String MAX_CAPACITY_EXCEEDED_EXCEPTION_MESSAGE = "New capacity exceeds the max. capacity of the sector.";

    @Mock
    private EventService eventServiceMock;

    @Mock
    private EventSectorRepository eventSectorRepositoryMock;

    @InjectMocks
    private EventSectorService eventSectorService;

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
     * Tests findByEventAndId in {@link EventSectorService} when {@link Event} with given ID contains
     * {@link EventSector} with given ID.
     */
    @Test
    public void findByEventAndId_shouldFindEventSector_whenEventWithGivenIdContainsSectorWithGivenId() {
        // Arrange
        EventSector eventSector = new EventSector();
        eventSector.setId(EVENT_SECTOR_ID);
        Event event = new Event();
        event.setId(EVENT_ID);
        event.getEventSectors().add(eventSector);
        eventSector.setEvent(event);
        when(this.eventServiceMock.findOne(EVENT_ID))
                .thenReturn(event);
        when(this.eventSectorRepositoryMock.findById(EVENT_SECTOR_ID))
                .thenReturn(Optional.of(eventSector));
        // Act
        EventSector foundEventSector = this.eventSectorService.findByEventAndId(EVENT_ID, EVENT_SECTOR_ID);

        // Assert
        verify(this.eventServiceMock).findOne(EVENT_ID);
        verify(this.eventSectorRepositoryMock).findById(EVENT_SECTOR_ID);
        assertNotNull(foundEventSector);
        assertEquals(EVENT_ID, foundEventSector.getEvent().getId());
        assertEquals(EVENT_SECTOR_ID, foundEventSector.getId());
    }

    /**
     * Tests findByEventAndId in {@link EventSectorService} when {@link Event} with given ID does not contain
     * {@link EventSector} with given ID. Should throw a {@link BadRequestException}.
     */
    @Test
    public void findByEventAndId_shouldThrowBadRequestException_whenEventWithGivenIdDoesNotContainSectorWithGivenId() {
        // Arrange
        this.exceptionRule.expect(BadRequestException.class);
        this.exceptionRule.expectMessage(EVENT_EXCEPTION_MESSAGE);

        EventSector eventSector = new EventSector();
        eventSector.setId(EVENT_SECTOR_ID_2);
        Event event = new Event();
        event.setId(EVENT_ID);
        event.getEventSectors().add(eventSector);
        eventSector.setEvent(event);
        when(this.eventServiceMock.findOne(EVENT_ID))
                .thenReturn(event);
        when(this.eventSectorRepositoryMock.findById(EVENT_SECTOR_ID))
                .thenReturn(Optional.of(eventSector));
        // Act
        this.eventSectorService.findByEventAndId(EVENT_ID, EVENT_SECTOR_ID);

        // Assert
        verify(this.eventServiceMock).findOne(EVENT_ID);
        verify(this.eventSectorRepositoryMock, never()).findById(any(Long.class));
    }

    /**
     * Tests findByEventAndId in {@link EventSectorService} when {@link Event} with given ID contains
     * {@link EventSector} with given ID, but that event sector is not present in database. Should throw a {@link BadRequestException}.
     */
    @Test
    public void findByEventAndId_shouldThrowBadRequestException_whenEventWithGivenIdContainsEventSectorWithGivenId_butIsNotPresentInDatabase() {
        // Arrange
        this.exceptionRule.expect(BadRequestException.class);
        this.exceptionRule.expectMessage(EVENT_SECTOR_EXCEPTION_MESSAGE);

        EventSector eventSector = new EventSector();
        eventSector.setId(EVENT_SECTOR_ID);
        Event event = new Event();
        event.setId(EVENT_ID);
        event.getEventSectors().add(eventSector);
        eventSector.setEvent(event);
        when(this.eventServiceMock.findOne(EVENT_ID))
                .thenReturn(event);
        when(this.eventSectorRepositoryMock.findById(EVENT_SECTOR_ID))
                .thenReturn(Optional.empty());
        // Act
        EventSector foundEventSector = this.eventSectorService.findByEventAndId(EVENT_ID, EVENT_SECTOR_ID);

        // Assert
        verify(this.eventServiceMock).findOne(EVENT_ID);
        verify(this.eventSectorRepositoryMock, never()).findById(any(Long.class));
        assertNotNull(foundEventSector);
        assertEquals(EVENT_ID, foundEventSector.getEvent().getId());
        assertEquals(EVENT_SECTOR_ID, foundEventSector.getId());
    }

    /**
     * Tests updateTicketPrice in {@link EventSectorService} when {@link EventSector} is present and ticket price is valid.
     * Should update the ticket price in {@link EventSector} and all {@link Ticket}s that are free.
     * Other ticket's prices should stay same.
     */
    @Test
    public void updateTicketPrice_shouldReturnUpdatedEventSectorAndUpdateTicketPrices_whenEventSectorIsPresentAndTicketPriceIsValid() {
        // Arrange
        EventSector eventSector = new EventSector();
        eventSector.setId(EVENT_SECTOR_ID);
        eventSector.setTicketPrice(OLD_TICKET_PRICE);
        Event event = new Event();
        event.setId(EVENT_ID);
        event.getEventSectors().add(eventSector);
        eventSector.setEvent(event);

        Ticket t1 = new Ticket(1L, 1, 1, OLD_TICKET_PRICE, TicketStatus.FREE, 1L, eventSector, event, null, null);
        Ticket t2 = new Ticket(2L, 1, 2, OLD_TICKET_PRICE, TicketStatus.FREE, 1L, eventSector, event, null, null);
        Ticket t3 = new Ticket(3L, 1, 3, OLD_TICKET_PRICE, TicketStatus.FREE, 1L, eventSector, event, null, null);
        Ticket t4 = new Ticket(3L, 1, 4, OLD_TICKET_PRICE, TicketStatus.RESERVED, 1L, eventSector, event, null, null);
        Ticket t5 = new Ticket(3L, 1, 5, OLD_TICKET_PRICE, TicketStatus.PAID, 1L, eventSector, event, null, null);
        Ticket t6 = new Ticket(3L, 1, 6, OLD_TICKET_PRICE, TicketStatus.USED, 1L, eventSector, event, null, null);
        eventSector.getTickets().addAll(Arrays.asList(t1, t2, t3, t4, t5, t6));

        when(this.eventServiceMock.findOne(EVENT_ID))
                .thenReturn(event);
        when(this.eventSectorRepositoryMock.findById(EVENT_SECTOR_ID))
                .thenReturn(Optional.of(eventSector));
        when(this.eventSectorRepositoryMock.save(eventSector))
                .thenReturn(eventSector);

        // Act
        EventSector updatedEventSector = this.eventSectorService.updateTicketPrice(EVENT_ID, EVENT_SECTOR_ID, NEW_TICKET_PRICE);

        // Assert
        verify(this.eventServiceMock).findOne(EVENT_ID);
        verify(this.eventSectorRepositoryMock).findById(EVENT_SECTOR_ID);
        assertNotNull(updatedEventSector);
        assertEquals(EVENT_ID, updatedEventSector.getEvent().getId());
        assertEquals(EVENT_SECTOR_ID, updatedEventSector.getId());
        assertEquals(NEW_TICKET_PRICE.longValue(), updatedEventSector.getTicketPrice().longValue());
        updatedEventSector.getTickets().forEach(ticket -> {
            if(ticket.getStatus().equals(TicketStatus.FREE)) {
                assertEquals(NEW_TICKET_PRICE.longValue(), ticket.getPrice().longValue());
            } else {
                assertEquals(OLD_TICKET_PRICE.longValue(), ticket.getPrice().longValue());
            }
        });
    }

    /**
     * Tests updateTicketPrice in {@link EventSectorService} when new ticket price is invalid.
     */
    @Test
    public void updateTicketPrice_shouldBadRequestException_whenTicketPriceIsInvalid() {
        // Arrange
        this.exceptionRule.expect(BadRequestException.class);
        this.exceptionRule.expectMessage(TICKET_PRICE_EXCEPTION_MESSAGE);

        // Act
        this.eventSectorService.updateTicketPrice(EVENT_ID, EVENT_SECTOR_ID, INVALID_TICKET_PRICE);

        // Assert
        verify(this.eventServiceMock, never()).findOne(EVENT_ID);
        verify(this.eventSectorRepositoryMock, never()).findById(EVENT_SECTOR_ID);
    }

    /**
     * Tests updateStatus in {@link EventSectorService} when status has changed to inactive and all tickets are free.
     */
    @Test
    public void updateStatus_shouldReturnUpdatedEventSector_whenStatusHasChangedToNotActiveAndAllTicketsAreFree() {
        // Arrange
        EventSector eventSector = new EventSector();
        eventSector.setId(EVENT_SECTOR_ID);
        eventSector.setTicketPrice(OLD_TICKET_PRICE);
        eventSector.setIsActive(true);
        Event event = new Event();
        event.setId(EVENT_ID);
        event.getEventSectors().add(eventSector);
        eventSector.setEvent(event);

        Ticket t1 = new Ticket(1L, 1, 1, OLD_TICKET_PRICE, TicketStatus.FREE, 1L, eventSector, event, null, null);
        Ticket t2 = new Ticket(2L, 1, 2, OLD_TICKET_PRICE, TicketStatus.FREE, 1L, eventSector, event, null, null);
        Ticket t3 = new Ticket(3L, 1, 3, OLD_TICKET_PRICE, TicketStatus.FREE, 1L, eventSector, event, null, null);
        eventSector.getTickets().addAll(Arrays.asList(t1, t2, t3));

        when(this.eventServiceMock.findOne(EVENT_ID))
                .thenReturn(event);
        when(this.eventSectorRepositoryMock.findById(EVENT_SECTOR_ID))
                .thenReturn(Optional.of(eventSector));
        when(this.eventSectorRepositoryMock.save(eventSector))
                .thenReturn(eventSector);

        // Act
        EventSector updatedEventSector = this.eventSectorService.updateStatus(EVENT_ID, EVENT_SECTOR_ID, false);

        // Assert
        verify(this.eventServiceMock).findOne(EVENT_ID);
        verify(this.eventSectorRepositoryMock).findById(EVENT_SECTOR_ID);
        verify(this.eventSectorRepositoryMock).save(eventSector);
        assertNotNull(updatedEventSector);
        assertEquals(EVENT_ID, updatedEventSector.getEvent().getId());
        assertEquals(EVENT_SECTOR_ID, updatedEventSector.getId());
        assertFalse(updatedEventSector.getIsActive());
        updatedEventSector.getTickets().forEach(ticket -> assertEquals(TicketStatus.USED, ticket.getStatus()));
    }

    /**
     * Tests updateStatus in {@link EventSectorService} when status has changed to inactive and there is
     * at least one ticket that is not free.
     */
    @Test
    public void updateStatus_shouldThrowBadRequestExceptionWhenThereIsATakenTicket() {
        // Arrange
        this.exceptionRule.expect(BadRequestException.class);
        this.exceptionRule.expectMessage(UPDATE_STATUS_EXCEPTION_MESSAGE);

        EventSector eventSector = new EventSector();
        eventSector.setId(EVENT_SECTOR_ID);
        eventSector.setTicketPrice(OLD_TICKET_PRICE);
        eventSector.setIsActive(true);
        Event event = new Event();
        event.setId(EVENT_ID);
        event.getEventSectors().add(eventSector);
        eventSector.setEvent(event);

        Ticket t1 = new Ticket(1L, 1, 1, OLD_TICKET_PRICE, TicketStatus.RESERVED, 1L, eventSector, event, null, null);
        Ticket t2 = new Ticket(2L, 1, 2, OLD_TICKET_PRICE, TicketStatus.RESERVED, 1L, eventSector, event, null, null);
        Ticket t3 = new Ticket(3L, 1, 3, OLD_TICKET_PRICE, TicketStatus.RESERVED, 1L, eventSector, event, null, null);
        eventSector.getTickets().addAll(Arrays.asList(t1, t2, t3));

        when(this.eventServiceMock.findOne(EVENT_ID))
                .thenReturn(event);
        when(this.eventSectorRepositoryMock.findById(EVENT_SECTOR_ID))
                .thenReturn(Optional.of(eventSector));
        when(this.eventSectorRepositoryMock.save(eventSector))
                .thenReturn(eventSector);

        // Act
        EventSector updatedEventSector = this.eventSectorService.updateStatus(EVENT_ID, EVENT_SECTOR_ID, false);

        // Assert
        verify(this.eventServiceMock).findOne(EVENT_ID);
        verify(this.eventSectorRepositoryMock).findById(EVENT_SECTOR_ID);
        verify(this.eventSectorRepositoryMock, never()).save(eventSector);
        assertNotNull(updatedEventSector);
        assertEquals(EVENT_ID, updatedEventSector.getEvent().getId());
        assertEquals(EVENT_SECTOR_ID, updatedEventSector.getId());
        assertTrue(updatedEventSector.getIsActive());
        updatedEventSector.getTickets().forEach(ticket -> assertEquals(TicketStatus.RESERVED, ticket.getStatus()));
    }

    /**
     * Tests updateStatus in {@link EventSectorService} when status has changed to active and all tickets are free.
     */
    @Test
    public void updateStatus_shouldReturnUpdatedEventSector_whenStatusHasChangedToActiveAndAllTicketsAreFree() {
        // Arrange
        EventSector eventSector = new EventSector();
        eventSector.setId(EVENT_SECTOR_ID);
        eventSector.setTicketPrice(OLD_TICKET_PRICE);
        eventSector.setIsActive(false);
        Event event = new Event();
        event.setId(EVENT_ID);
        event.getEventSectors().add(eventSector);
        eventSector.setEvent(event);

        Ticket t1 = new Ticket(1L, 1, 1, OLD_TICKET_PRICE, TicketStatus.FREE, 1L, eventSector, event, null, null);
        Ticket t2 = new Ticket(2L, 1, 2, OLD_TICKET_PRICE, TicketStatus.FREE, 1L, eventSector, event, null, null);
        Ticket t3 = new Ticket(3L, 1, 3, OLD_TICKET_PRICE, TicketStatus.FREE, 1L, eventSector, event, null, null);
        eventSector.getTickets().addAll(Arrays.asList(t1, t2, t3));

        when(this.eventServiceMock.findOne(EVENT_ID))
                .thenReturn(event);
        when(this.eventSectorRepositoryMock.findById(EVENT_SECTOR_ID))
                .thenReturn(Optional.of(eventSector));
        when(this.eventSectorRepositoryMock.save(eventSector))
                .thenReturn(eventSector);

        // Act
        EventSector updatedEventSector = this.eventSectorService.updateStatus(EVENT_ID, EVENT_SECTOR_ID, true);

        // Assert
        verify(this.eventServiceMock).findOne(EVENT_ID);
        verify(this.eventSectorRepositoryMock).findById(EVENT_SECTOR_ID);
        verify(this.eventSectorRepositoryMock).save(eventSector);
        assertNotNull(updatedEventSector);
        assertEquals(EVENT_ID, updatedEventSector.getEvent().getId());
        assertEquals(EVENT_SECTOR_ID, updatedEventSector.getId());
        assertTrue(updatedEventSector.getIsActive());
        updatedEventSector.getTickets().forEach(ticket -> assertEquals(TicketStatus.FREE, ticket.getStatus()));
    }

    /**
     * Tests updateCapacity in {@link EventSectorService} when new capacity is invalid.
     */
    @Test
    public void updateCapacity_shouldThrowBadRequestException_whenNewCapacityIsInvalid() {
        // Arrange
        this.exceptionRule.expect(BadRequestException.class);
        this.exceptionRule.expectMessage(INVALID_CAPACITY_EXCEPTION_MESSAGE);

        // Act
        this.eventSectorService.updateCapacity(EVENT_ID, EVENT_SECTOR_ID, 0);
    }

    /**
     * Tests updateCapacity in {@link EventSectorService} when seats are numerated.
     */
    @Test
    public void updateCapacity_shouldThrowBadRequestException_whenSeatsAreNumerated() {
        // Arrange
        this.exceptionRule.expect(BadRequestException.class);
        this.exceptionRule.expectMessage(NUMERATED_SEATS_EXCEPTION_MESSAGE);

        EventSector eventSector = new EventSector();
        eventSector.setId(EVENT_SECTOR_ID);
        eventSector.setTicketPrice(OLD_TICKET_PRICE);
        eventSector.setIsActive(true);
        eventSector.setNumeratedSeats(true);
        Event event = new Event();
        event.setId(EVENT_ID);
        event.getEventSectors().add(eventSector);
        eventSector.setEvent(event);

        Ticket t1 = new Ticket(1L, 1, 1, OLD_TICKET_PRICE, TicketStatus.RESERVED, 1L, eventSector, event, null, null);
        Ticket t2 = new Ticket(2L, 1, 2, OLD_TICKET_PRICE, TicketStatus.RESERVED, 1L, eventSector, event, null, null);
        Ticket t3 = new Ticket(3L, 1, 3, OLD_TICKET_PRICE, TicketStatus.RESERVED, 1L, eventSector, event, null, null);
        eventSector.getTickets().addAll(Arrays.asList(t1, t2, t3));

        when(this.eventServiceMock.findOne(EVENT_ID))
                .thenReturn(event);
        when(this.eventSectorRepositoryMock.findById(EVENT_SECTOR_ID))
                .thenReturn(Optional.of(eventSector));

        // Act
        this.eventSectorService.updateCapacity(EVENT_ID, EVENT_SECTOR_ID, 5);
    }

    /**
     * Tests updateCapacity in {@link EventSectorService} when new capacity exceeds the max. capacity of the sector.
     */
    @Test
    public void updateCapacity_shouldThrowBadRequestException_whenMaxCapacityExceeded() {
        // Arrange
        this.exceptionRule.expect(BadRequestException.class);
        this.exceptionRule.expectMessage(MAX_CAPACITY_EXCEEDED_EXCEPTION_MESSAGE);

        EventSector eventSector = new EventSector();
        eventSector.setId(EVENT_SECTOR_ID);
        eventSector.setTicketPrice(OLD_TICKET_PRICE);
        eventSector.setIsActive(true);
        eventSector.setNumeratedSeats(false);
        eventSector.setCapacity(1);
        Sector sector = new Sector();
        sector.setMaxCapacity(2);
        eventSector.setSector(sector);
        Event event = new Event();
        event.setId(EVENT_ID);
        event.getEventSectors().add(eventSector);
        eventSector.setEvent(event);

        Ticket t1 = new Ticket(1L, 1, 1, OLD_TICKET_PRICE, TicketStatus.RESERVED, 1L, eventSector, event, null, null);
        Ticket t2 = new Ticket(2L, 1, 2, OLD_TICKET_PRICE, TicketStatus.RESERVED, 1L, eventSector, event, null, null);
        Ticket t3 = new Ticket(3L, 1, 3, OLD_TICKET_PRICE, TicketStatus.RESERVED, 1L, eventSector, event, null, null);
        eventSector.getTickets().addAll(Arrays.asList(t1, t2, t3));

        when(this.eventServiceMock.findOne(EVENT_ID))
                .thenReturn(event);
        when(this.eventSectorRepositoryMock.findById(EVENT_SECTOR_ID))
                .thenReturn(Optional.of(eventSector));

        // Act
        this.eventSectorService.updateCapacity(EVENT_ID, EVENT_SECTOR_ID, 5);
    }
}
