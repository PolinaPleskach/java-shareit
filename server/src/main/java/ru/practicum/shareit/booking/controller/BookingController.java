package ru.practicum.shareit.booking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingFullDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    public static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final BookingService bookingService;

    @PostMapping
    public BookingFullDto create(@RequestHeader(USER_ID_HEADER) Long userId,
                                 @Valid @RequestBody BookingDto bookingDto) {
        log.info("Пришел POST запрос /bookings с телом: {}", bookingDto);
        BookingFullDto createdBooking = bookingService.create(userId, bookingDto);
        log.info("Отправлен ответ /bookings с телом: {}", createdBooking);
        return createdBooking;
    }

    @PatchMapping("/{bookingId}")
    public BookingFullDto approved(@RequestHeader(USER_ID_HEADER) Long userId,
                                   @PathVariable Long bookingId, @RequestParam Boolean approved) {
        log.info("Пришел PATCH запрос /bookings/{} с параметром approved: {} от пользователя с ID: {}", bookingId, approved, userId);
        BookingFullDto updatedBooking = bookingService.approved(userId, bookingId, approved);
        log.info("Отправлен ответ /bookings/{} с телом: {}", bookingId, updatedBooking);
        return updatedBooking;
    }

    @GetMapping("/{bookingId}")
    public BookingFullDto getById(@RequestHeader(USER_ID_HEADER) Long userId,
                                  @PathVariable("bookingId") Long bookingId) {
        log.info("Пришел GET запрос /bookings/{} от пользователя с ID: {}", bookingId, userId);
        BookingFullDto booking = bookingService.getById(userId, bookingId);
        log.info("Отправлен ответ /bookings/{} с телом: {}", bookingId, booking);
        return booking;
    }

    @GetMapping
    public List<BookingFullDto> getAllByBooker(@RequestHeader(USER_ID_HEADER) Long userId,
                                               @RequestParam(defaultValue = "ALL") String state) {
        log.info("Пришел GET запрос /bookings с параметром state: {} от пользователя с ID: {}", state, userId);
        List<BookingFullDto> bookings = bookingService.getAllByBooker(userId, state);
        log.info("Отправлен ответ /bookings с телом: {}", bookings);
        return bookings;
    }

    @GetMapping("/owner")
    public List<BookingFullDto> getAllByOwner(@RequestHeader(USER_ID_HEADER) Long ownerId,
                                              @RequestParam(defaultValue = "ALL") String state) {
        log.info("Пришел GET запрос /bookings/owner с параметром state: {} от пользователя с ID: {}", state, ownerId);
        List<BookingFullDto> bookings = bookingService.getAllByOwner(ownerId, state);
        log.info("Отправлен ответ /bookings/owner с телом: {}", bookings);
        return bookings;
    }
}
