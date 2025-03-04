package ru.practicum.shareit.user.validation;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

@Component
public class UserValidation {

    public void checkUserFields(UserDto userDto) {
        if (userDto == null) {
            throw new ValidationException("Нет данных");
        }

        if (userDto.getName() == null || userDto.getName().isBlank()) {
            throw new ValidationException("Имя пользователя не указано");
        }

        if (userDto.getEmail() == null || userDto.getEmail().isBlank() || !(userDto.getEmail().contains("@"))) {
            throw new ValidationException("Почта не может не содержать символ @ или быть пустой");
        }
    }
}
