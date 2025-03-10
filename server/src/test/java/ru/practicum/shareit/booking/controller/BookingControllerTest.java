package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingFullDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
@FieldDefaults(level = AccessLevel.PRIVATE)
class BookingControllerTest {
    @MockBean
    BookingService bookingService;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    BookingDto bookingDto;
    BookingFullDto bookingFullDto;
    static final String HEADER = "X-Sharer-User-Id";

    @BeforeEach
    public void setUp() {
        bookingFullDto = new BookingFullDto();
        bookingFullDto.setId(1L);
        bookingFullDto.setStatus(BookingStatus.WAITING);
        bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(LocalDateTime.now());
        bookingDto.setEnd(LocalDateTime.now().plusDays(1));
    }

    @Test
    @DisplayName("Проверяем создание бронирования")
    void testCreateBooking() throws Exception {
        when(bookingService.create(anyLong(), any(BookingDto.class)))
                .thenReturn(bookingFullDto);
        final String bookingRequestJson = objectMapper.writeValueAsString(bookingFullDto);
        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookingRequestJson)
                        .header(HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    @DisplayName("Проверяем создание бронирования")
    void testApproved() throws Exception {
        bookingFullDto.setStatus(BookingStatus.APPROVED);
        when(bookingService.approved(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingFullDto);
        mockMvc.perform(patch("/bookings/1")
                        .param("approved", "true")
                        .header(HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    @DisplayName("Проверяем поиск бронирования по id")
    void testGetById() throws Exception {
        when(bookingService.getById(anyLong(), anyLong()))
                .thenReturn(bookingFullDto);
        mockMvc.perform(get("/bookings/1")
                        .header(HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }


    @Test
    void testGetAllByBooker() throws Exception {
        when(bookingService.getAllByBooker(anyLong(), anyString()))
                .thenReturn(Collections.singletonList(bookingFullDto));
        mockMvc.perform(get("/bookings")
                        .header(HEADER, 1)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }


    @Test
    void testGetAllByOwner() throws Exception {
        when(bookingService.getAllByOwner(anyLong(), anyString()))
                .thenReturn(Collections.singletonList(bookingFullDto));
        mockMvc.perform(get("/bookings/owner")
                        .header(HEADER, 1)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }
}
