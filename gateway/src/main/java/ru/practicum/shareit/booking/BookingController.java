package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    public static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final BookingClient bookingClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> create(@RequestHeader(USER_ID_HEADER) long userId,
                                         @Valid @RequestBody BookItemRequestDto requestDto) {
        log.info("Пришел POST запрос /bookings с телом: {}", requestDto);
        ResponseEntity<Object> response = bookingClient.bookItem(userId, requestDto);
        log.info("Отправлен ответ POST /bookings с телом: {}", response.getBody());
        return response;
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approved(@RequestHeader(USER_ID_HEADER) Long userId,
                                           @PathVariable Long bookingId,
                                           @RequestParam Boolean approved) {
        log.info("Пришел PATCH запрос /bookings/{} с параметром approved: {} от пользователя {}",
                bookingId, approved, userId);
        ResponseEntity<Object> response = bookingClient.approved(userId, bookingId, approved);
        log.info("Отправлен ответ PATCH /bookings/{} с телом: {}", bookingId, response.getBody());
        return response;
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getById(@RequestHeader(USER_ID_HEADER) Long userId,
                                          @PathVariable("bookingId") Long bookingId) {
        log.info("Пришел GET запрос /bookings/{} от пользователя {}", bookingId, userId);
        ResponseEntity<Object> response = bookingClient.getBookingById(userId, bookingId);
        log.info("Отправлен ответ GET /bookings/{} с телом: {}", bookingId, response.getBody());
        return response;
    }

    @GetMapping
    public ResponseEntity<Object> getAllByBooker(@RequestHeader(USER_ID_HEADER) Long userId,
                                                 @RequestParam(defaultValue = "ALL") String state) {
        log.info("Пришел GET запрос /bookings с state: {} от пользователя {}", state, userId);
        ResponseEntity<Object> response = bookingClient.getAllByBooker(userId, state);
        log.info("Отправлен ответ GET /bookings с телом: {}", response.getBody());
        return response;
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllByOwner(@RequestHeader(USER_ID_HEADER) Long ownerId,
                                                @RequestParam(defaultValue = "ALL") String state) {
        log.info("Пришел GET запрос /bookings/owner с state: {} от пользователя {}", state, ownerId);
        ResponseEntity<Object> response = bookingClient.getAllByOwner(ownerId, state);
        log.info("Отправлен ответ GET /bookings/owner с телом: {}", response.getBody());
        return response;
    }
}