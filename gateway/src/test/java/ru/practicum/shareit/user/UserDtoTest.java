package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.ShareItGateway;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(classes = ShareItGateway.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDtoTest {

    final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("Сериализация userDto")
    void serializeJsonTest() throws Exception {
        final UserDto userDto = new UserDto();
        userDto.setName("Polina");
        userDto.setEmail("polina@gmail.com");
        String json = objectMapper.writeValueAsString(userDto);
        assertThat(json).contains("\"name\":\"Polina\"", "\"email\":\"polina@gmail.com\"");
    }

    @Test
    @Order(2)
    @DirtiesContext
    @DisplayName("Дессериализация userDto")
    void deserializeJsonTest() throws Exception {
        final String json = "{\"id\":1,\"name\":\"Polina\",\"email\":\"polina@gmail.com\"}";
        final UserDto userDto = objectMapper.readValue(json, UserDto.class);
        assertThat(userDto.getName()).isEqualTo("Polina");
    }

    @Test
    @DisplayName("Валидация userDto")
    void validationTest() {

        final UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("Polina");
        userDto.setEmail("polina@gmail.com");
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Валидация userDto, неверный пользователь")
    public void invalidTest() {
        final UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("");
        userDto.setEmail("polina@gmail.com");
        var invalidConstraints = validator.validate(userDto);
        assertThat(invalidConstraints).isNotEmpty();
    }
}
