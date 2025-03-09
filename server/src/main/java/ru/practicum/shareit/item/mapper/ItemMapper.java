package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentFullDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemFullDto;
import ru.practicum.shareit.item.dto.ItemSimpleDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public class ItemMapper {
    public static ItemDto mapToItemDto(Item item) {
        if (item.getItemRequest() != null) {
            return new ItemDto(
                    item.getId(),
                    item.getName(),
                    item.getDescription(),
                    item.getAvailable(),
                    item.getItemRequest().getId()
            );
        } else {
            return new ItemDto(
                    item.getId(),
                    item.getName(),
                    item.getDescription(),
                    item.getAvailable()
            );
        }
    }

    public static Item mapToItem(User user, ItemDto itemDto) {
        return new Item(
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                user,
                null
        );
    }

    public static ItemFullDto mapToItemFullDto(Item item, Booking lastBooking, Booking nextBooking, List<Comment> comments) {
        List<CommentFullDto> listFullDto = comments.stream().map(CommentMapper::mapToCommentFullDto).toList();
        return new ItemFullDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                listFullDto,
                lastBooking,
                nextBooking
        );
    }

    public static ItemSimpleDto mapToItemSimpleDto(Item item) {
        return new ItemSimpleDto(
                item.getId(),
                item.getName(),
                item.getOwner().getId()
        );
    }
}
