package ru.practicum.shareit.booking.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByIdIn(List<Long> itemId);

    List<Booking> findAllByItemId(Long itemId);

    List<Booking> getAllByBookerIdOrderByStartBookingDesc(Long userId);

    List<Booking> findAllByItemOwnerIdOrderByStartBookingDesc(Long userId);

    @Query("""
            Select b
            From Booking as b
            WHERE b.booker.id = :userId
            AND current_timestamp between b.startBooking and b.endBooking
            ORDER BY b.startBooking DESC
            """
    )
    List<Booking> getAllByBookerIdAndCurrentTime(Long userId);

    List<Booking> findAllByItemOwnerIdAndStartBookingBeforeAndEndBookingAfterOrderByStartBookingDesc(Long ownerId, LocalDateTime time, LocalDateTime time2);

    @Query("""
            Select b
            From Booking as b
            WHERE b.booker.id = :userId
            AND current_timestamp < b.endBooking
            ORDER BY b.startBooking DESC
            """)
    List<Booking> getAllByBookerIdAndPastTime(Long userId);

    List<Booking> findAllByItemOwnerIdAndEndBookingBeforeOrderByStartBookingDesc(Long ownerId, LocalDateTime time);

    @Query("""
            Select b
            From Booking as b
            WHERE b.booker.id = :userId
            AND current_timestamp < b.startBooking
            ORDER BY b.startBooking DESC
            """)
    List<Booking> getAllByBookerIdAndFutureTime(Long userId);

    List<Booking> findAllByItemOwnerIdAndStartBookingAfterOrderByStartBookingDesc(Long ownerId, LocalDateTime time);

    List<Booking> findAllByItemIdAndEndBookingBefore(Long itemId, LocalDateTime time);

    List<Booking> getAllByBookerIdAndStatusIs(Long userId, BookingStatus status);

    List<Booking> findAllByItemOwnerIdAndStatusIsOrderByStartBookingDesc(Long userId, BookingStatus status);

    List<Booking> findAllByIdInAndEndBookingBefore(List<Long> itemsId, LocalDateTime time);

    List<Booking> getALLByItemIdAndBookerIdAndStatusIsOrderByEndBookingDesc(Long userId, Long itemId, BookingStatus status);
}
