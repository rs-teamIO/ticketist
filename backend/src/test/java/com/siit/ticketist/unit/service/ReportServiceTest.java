package com.siit.ticketist.unit.service;

import com.siit.ticketist.repository.EventRepository;
import com.siit.ticketist.repository.VenueRepository;
import com.siit.ticketist.service.ReportService;
import org.junit.Before;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ReportServiceTest {

    @Mock
    private VenueRepository venueRepository;

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private ReportService reportService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }
}
