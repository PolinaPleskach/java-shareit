package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;

@Data
@EqualsAndHashCode(of = {"id"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewUserRequest {
    Long id;
    @NotBlank(message = "Необходимо указать имя пользователя")
    String name;
    @NotBlank(message = "Необходимо указать электронную почту")
    @Email(message = "Электронная почта должна быть в формате 1234@yandex.ru")
    String email;
}
