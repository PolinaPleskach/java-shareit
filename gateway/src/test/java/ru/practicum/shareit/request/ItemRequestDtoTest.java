package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.ShareItGateway;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(classes = ShareItGateway.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequestDtoTest {

    ObjectMapper objectMapper;
    Validator validator;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Сериализация объекта ItemRequestDto")
    public void serializeJsonTest() throws Exception {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("lovingKitty");
        String jsonContent = objectMapper.writeValueAsString(itemRequestDto);
        assertThat(jsonContent).contains("\"description\":\"lovingKitty\"");
    }

    @Test
    @DisplayName("Валидация ItemRequestDto")
    public void validationTest() {
        ItemRequestDto validRequest = new ItemRequestDto();
        validRequest.setDescription("lovingKitty");
        ItemRequestDto invalidRequest = new ItemRequestDto();
        invalidRequest.setDescription("");
        var validConstraints = validator.validate(validRequest);
        var invalidConstraints = validator.validate(invalidRequest);
        assertThat(validConstraints).isEmpty();
        assertThat(invalidConstraints).isNotEmpty();
    }
}