package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingFullDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

public class BookingMapper {
    public static Booking mapToBooking(BookingDto bookingDto, User user, Item item) {
        return new Booking(
                bookingDto.getStart(),
                bookingDto.getEnd(),
                item,
                user
        );
    }

    public static BookingFullDto mapToBookingFullDto(Booking booking, User user, Item item) {
        UserDto userdto = UserMapper.mapToUserDto(user);
        return new BookingFullDto(
                booking.getId(),
                booking.getStartBooking(),
                booking.getEndBooking(),
                new ItemDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable()),
                userdto,
                booking.getStatus()
        );
    }
}
