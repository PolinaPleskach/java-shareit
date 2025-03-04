package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingFullDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public BookingFullDto create(Long userId, BookingDto bookingDto) {
        log.info("Создание бронирования для пользователя с ID: {}", userId);

        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Не существует такого пользователя с id: " + userId));
        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(() ->
                new NotFoundException("Не существует такого предмета c id " + bookingDto.getItemId()));

        if (!bookingDto.isBeforeEnd(bookingDto)) {
            throw new ValidationException("Вещь забронирована");
        }
        if (!item.getAvailable()) {
            throw new ValidationException("Вещь забронирована");
        }

        Booking booking = BookingMapper.mapToBooking(bookingDto, user, item);
        bookingRepository.save(booking);

        log.info("Бронирование успешно создано: {}", booking);
        return BookingMapper.mapToBookingFullDto(booking, user, item);
    }

    @Override
    @Transactional
    public BookingFullDto approved(Long userId, Long bookingId, Boolean approved) {
        log.info("Подтверждение бронирования с ID: {} пользователем с ID: {}", bookingId, userId);

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Не существует такого бронирования по id " + bookingId));
        if (!(userId.equals(booking.getItem().getOwner().getId()))) {
            throw new ValidationException("Только собственник может подтвердить бронирование");
        }

        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        Booking approvedBooking = bookingRepository.save(booking);
        log.info("Бронирование с ID: {} успешно подтверждено: {}", bookingId, approvedBooking);

        return BookingMapper.mapToBookingFullDto(approvedBooking, booking.getBooker(), booking.getItem());
    }

    @Override
    @Transactional(readOnly = true)
    public BookingFullDto getById(Long userId, Long bookingId) {
        log.info("Получение информации о бронировании с ID: {} для пользователя с ID: {}", bookingId, userId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Нет такого бронирования по id " + bookingId));
        if (!(userId.equals(booking.getBooker().getId())) && !(userId.equals(booking.getItem().getOwner().getId()))) {
            throw new ValidationException("Просматривать info может владелец вещи или создатель брони");
        }

        log.info("Информация о бронировании с ID: {} успешно получена", bookingId);
        return BookingMapper.mapToBookingFullDto(booking, booking.getBooker(), booking.getItem());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingFullDto> getAllByBooker(Long userId, String state) {
        log.info("Получение всех бронирований для пользователя с ID: {} и состоянием: {}", userId, state);

        State bookingState = from(state);
        userService.findUser(userId);

        List<Booking> bookings;

        assert bookingState != null;
        bookings = switch (bookingState) {
            case ALL -> bookingRepository.getAllByBookerIdOrderByStartBookingDesc(userId);
            case CURRENT -> bookingRepository.getAllByBookerIdAndCurrentTime(userId);
            case PAST -> bookingRepository.getAllByBookerIdAndPastTime(userId);
            case FUTURE -> bookingRepository.getAllByBookerIdAndFutureTime(userId);
            case WAITING -> bookingRepository.getAllByBookerIdAndStatusIs(userId, BookingStatus.WAITING);
            case REJECTED -> bookingRepository.getAllByBookerIdAndStatusIs(userId, BookingStatus.REJECTED);
            default -> throw new ValidationException("Некорректное значение state %s" + bookingState);
        };

        log.info("Бронирования для пользователя с ID: {} успешно получены", userId);
        return bookings.stream().map(i -> BookingMapper.mapToBookingFullDto(i, i.getBooker(), i.getItem())).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingFullDto> getAllByOwner(Long ownerId, String state) {
        log.info("Получение всех бронирований для владельца с ID: {} и состоянием: {}", ownerId, state);

        State bookingState = from(state);
        userService.findUser(ownerId);

        List<Booking> bookings;

        assert bookingState != null;
        bookings = switch (bookingState) {
            case ALL -> bookingRepository.findAllByItemOwnerIdOrderByStartBookingDesc(ownerId);
            case CURRENT ->
                    bookingRepository.findAllByItemOwnerIdAndStartBookingBeforeAndEndBookingAfterOrderByStartBookingDesc(ownerId, LocalDateTime.now(), LocalDateTime.now());
            case PAST ->
                    bookingRepository.findAllByItemOwnerIdAndEndBookingBeforeOrderByStartBookingDesc(ownerId, LocalDateTime.now());
            case FUTURE ->
                    bookingRepository.findAllByItemOwnerIdAndStartBookingAfterOrderByStartBookingDesc(ownerId, LocalDateTime.now());
            case WAITING ->
                    bookingRepository.findAllByItemOwnerIdAndStatusIsOrderByStartBookingDesc(ownerId, BookingStatus.WAITING);
            case REJECTED ->
                    bookingRepository.findAllByItemOwnerIdAndStatusIsOrderByStartBookingDesc(ownerId, BookingStatus.REJECTED);
            default -> throw new ValidationException("Некорректное значение state %s" + bookingState);
        };

        log.info("Бронирования для владельца с ID: {} успешно получены", ownerId);
        return bookings.stream().map(i -> BookingMapper.mapToBookingFullDto(i, i.getBooker(), i.getItem())).toList();
    }

    private State from(String state) {
        state = state.toUpperCase();
        for (State value : State.values()) {
            if (value.name().equals(state)) {
                return value;
            } else {
                throw new ValidationException("Значение не соответствует допустимому %s " + state);
            }
        }
        return null;
    }
}
