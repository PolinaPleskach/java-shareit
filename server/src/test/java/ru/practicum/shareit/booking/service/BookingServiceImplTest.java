package ru.practicum.shareit.booking.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingFullDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class BookingServiceImplTest {
    @Mock
    UserService userService;
    BookingService bookingService;
    BookingMapper bookingMapper;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ItemRepository itemRepository;
    User user1;
    Item item1;
    Item item2;
    Booking booking;
    Booking booking1;
    BookingDto bookingDto;

    @BeforeEach
    public void setUp() {
        bookingMapper = new BookingMapper();
        bookingService = new BookingServiceImpl(userService, bookingRepository, itemRepository, userRepository);
        user1 = new User();
        user1.setName("User");
        user1.setEmail("User@yandex.ru");
        user1.setId(1L);

        item1 = new Item();
        item1.setName("Vasilek");
        item1.setDescription("lovingKitty");
        item1.setOwner(user1);
        item1.setAvailable(true);

        item2 = new Item();
        item2.setName("Mara");
        item2.setDescription("lovingCat");
        item2.setOwner(user1);
        item2.setAvailable(false);

        bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(LocalDateTime.now().plusDays(2));
        bookingDto.setEnd(LocalDateTime.now().plusDays(5));

        booking = new Booking();
        booking.setItem(item1);
        booking.setBooker(user1);
        booking.setId(1L);
        booking.setStatus(BookingStatus.WAITING);
        booking.setStartBooking(LocalDateTime.now().plusDays(2));
        booking.setEndBooking(LocalDateTime.now().plusDays(5));

        booking1 = new Booking();
        booking1.setItem(item1);
        booking1.setBooker(user1);
        booking1.setId(2L);
        booking1.setStatus(BookingStatus.WAITING);
        booking1.setStartBooking(LocalDateTime.now().minusDays(10));
        booking1.setEndBooking(LocalDateTime.now().minusDays(5));
    }

    @Test
    @DisplayName("BookingService_createNotUser")
    void testCreateNotUser() {

        assertThrows(
                NotFoundException.class,
                () -> bookingService.create(1L, bookingDto)
        );
    }

    @Test
    @DisplayName("BookingService_createNotItem")
    void testCreateNotItem() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));

        assertThrows(
                NotFoundException.class,
                () -> bookingService.create(1L, bookingDto)
        );
    }

    @Test
    @DisplayName("BookingService_createNotAvailable")
    void testCreateNotAvailable() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item2));

        assertThrows(
                ValidationException.class,
                () -> bookingService.create(1L, bookingDto)
        );
    }

    @Test
    @DisplayName("BookingService_createNotGoodTime")
    void testCreateNotGoodTime() {

        final BookingDto bookingDto;
        bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(LocalDateTime.now().plusDays(7));
        bookingDto.setEnd(LocalDateTime.now().plusDays(5));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));

        assertThrows(
                ValidationException.class,
                () -> bookingService.create(1L, bookingDto)
        );
    }

    @Test
    @DisplayName("BookingService_createEqualsTime")
    void testCreateEqualsTime() {

        final LocalDateTime time = LocalDateTime.now().plusDays(5);

        final BookingDto bookingDto;
        bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(time);
        bookingDto.setEnd(time);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));

        assertThrows(
                ValidationException.class,
                () -> bookingService.create(1L, bookingDto)
        );
    }

    @Test
    @DisplayName("BookingService_approvedNotBooking")
    void testApprovedNotBooking() {

        assertThrows(
                NotFoundException.class,
                () -> bookingService.approved(1L, 1L, true)
        );
    }

    @Test
    @DisplayName("BookingService_approvedUserNotOwner")
    void testApprovedUserNotOwner() {

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(
                ValidationException.class,
                () -> bookingService.approved(2L, 1L, true)
        );
    }

    @Test
    @DisplayName("BookingService_approvedTrue")
    void testApprovedTrue() {

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking)).thenReturn(booking);

        final BookingFullDto bookingResponce = bookingService.approved(1L, 1L, true);

        assertEquals(BookingStatus.APPROVED, bookingResponce.getStatus());
        assertEquals("User", bookingResponce.getBooker().getName());
        assertEquals("Vasilek", bookingResponce.getItem().getName());
    }

    @Test
    @DisplayName("BookingService_approvedFalse")
    void testApprovedFalse() {

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking)).thenReturn(booking);

        final BookingFullDto bookingResponse = bookingService.approved(1L, 1L, false);

        assertEquals(BookingStatus.REJECTED, bookingResponse.getStatus());
        assertEquals("User", bookingResponse.getBooker().getName());
        assertEquals("Vasilek", bookingResponse.getItem().getName());
    }

    @Test
    @DisplayName("BookingService_findByIdNotBooking")
    void testFindByIdNotBooking() {

        assertThrows(
                NotFoundException.class,
                () -> bookingService.getById(1L, 1L)
        );
    }

    @Test
    @DisplayName("BookingService_findByIdUserNotOwner")
    void testFindByIdUserNotOwner() {

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(
                ValidationException.class,
                () -> bookingService.getById(2L, 1L)
        );
    }

    @Test
    @DisplayName("BookingService_findById")
    void testFindById() {

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        final BookingFullDto bookingResponse = bookingService.getById(1L, 1L);

        assertEquals("User", bookingResponse.getBooker().getName());
        assertEquals("Vasilek", bookingResponse.getItem().getName());
    }

    @Test
    @DisplayName("BookingService_findAllByUserIdAll")
    void testFindAllByUserIdAll() {
        when(bookingRepository.getAllByBookerIdOrderByStartBookingDesc(1L)).thenReturn(List.of(booking, booking1));

        final List<BookingFullDto> bookingResponses = bookingService.getAllByBooker(1L, "all");

        assertEquals(2, bookingResponses.size());
    }

    @Test
    @DisplayName("BookingService_findAllByUserIdValid")
    void testFindAllByUserIdValid() {


        assertThrows(
                ValidationException.class,
                () -> bookingService.getAllByBooker(1L, "pupu")
        );
    }

    @Test
    @DisplayName("BookingService_findAllByOwnerIdAll")
    void testFindAllByOwnerIdAll() {
        when(bookingRepository.findAllByItemOwnerIdOrderByStartBookingDesc(1L)).thenReturn(List.of(booking, booking1));

        final List<BookingFullDto> bookingResponses = bookingService.getAllByOwner(1L, "all");

        assertEquals(2, bookingResponses.size());
    }

    @Test
    @DisplayName("BookingService_findAllByOwnerIdValid")
    void testFindAllByOwnerIdValid() {


        assertThrows(
                ValidationException.class,
                () -> bookingService.getAllByOwner(1L, "pupu")
        );
    }
}