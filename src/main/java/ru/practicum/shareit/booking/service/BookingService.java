package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingFullDto;

import java.util.List;

public interface BookingService {
    BookingFullDto create(Long userId, BookingDto bookingDto);

    BookingFullDto approved(Long userId, Long bookingId, Boolean approved);

    BookingFullDto getById(Long userId, Long bookingId);

    List<BookingFullDto> getAllByBooker(Long userId, String state);

    List<BookingFullDto> getAllByOwner(Long ownerId, String state);
}
