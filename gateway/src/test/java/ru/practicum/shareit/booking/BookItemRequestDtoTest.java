package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.ShareItGateway;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;


@JsonTest
@ContextConfiguration(classes = ShareItGateway.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class BookItemRequestDtoTest {

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("Сериализация")
    void serializeJsonTest() throws Exception {
        final BookItemRequestDto bookRequestDto = new BookItemRequestDto();
        bookRequestDto.setItemId(1);
        bookRequestDto.setStart(LocalDateTime.now().plusDays(1));
        bookRequestDto.setEnd(LocalDateTime.now().plusDays(3));
        String json = objectMapper.writeValueAsString(bookRequestDto);
        assertThat(json).contains("\"itemId\":1");
    }

    @Test
    @DisplayName("Дессериализация")
    void deserializeJsonTest() throws Exception {
        String json = "{\"itemId\":1,\"start\":\"2025-03-02T10:00:00\",\"end\":\"2025-03-03T10:00:00\"}";
        BookItemRequestDto bookRequestDto = objectMapper.readValue(json, BookItemRequestDto.class);
        assertThat(bookRequestDto.getItemId()).isEqualTo(1);
    }
}
