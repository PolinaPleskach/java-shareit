package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.item.dto.CommentDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@FieldDefaults(level = AccessLevel.PRIVATE)
class CommentDtoTest {
    ObjectMapper objectMapper;
    Validator validator;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Сериализация объекта CommentDto")
    void serializeJsonTest() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Vasilek");
        String jsonContent = objectMapper.writeValueAsString(commentDto);
        assertThat(jsonContent).contains("\"text\":\"Vasilek\"");
    }

    @Test
    @DisplayName("Валидация поля")
    void validationTest() {
        CommentDto validRequest = new CommentDto();
        validRequest.setText("Vasilek");
        CommentDto invalidRequest = new CommentDto();
        invalidRequest.setText("");
        var validConstraints = validator.validate(validRequest);
        var invalidConstraints = validator.validate(invalidRequest);
        assertThat(validConstraints).isEmpty();
        assertThat(invalidConstraints).isNotEmpty();
    }
}