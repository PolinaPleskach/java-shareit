package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDtoTest {

    final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("Сериализация объекта itemDto")
    void serializeJsonTest() throws Exception {
        final ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Vasilek");
        itemDto.setDescription("lovingKitty");
        itemDto.setAvailable(true);
        itemDto.setRequestId(1L);
        String json = objectMapper.writeValueAsString(itemDto);
        assertThat(json).contains("\"name\":\"Vasilek\"");
    }

    @Test
    @DisplayName("Десериализация объекта CommentDto")
    void deserializeJsonTest() throws Exception {
        String json = "{\"id\":1,\"name\":\"Vasilek\",\"description\":\"lovingKitty\",\"available\":true,\"requestId\":1}";
        ItemDto itemDto = objectMapper.readValue(json, ItemDto.class);
        assertThat(itemDto.getName()).isEqualTo("Vasilek");
    }

    @Test
    @DisplayName("Валидация itemDto")
    void validationTest() {
        final ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Vasilek");
        itemDto.setDescription("lovingKitty");
        itemDto.setAvailable(true);
        itemDto.setRequestId(1L);
        Set<ConstraintViolation<ItemDto>> violations = validator.validate(itemDto);
        assertThat(violations).isEmpty();
    }
}
