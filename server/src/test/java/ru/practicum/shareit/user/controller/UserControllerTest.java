package ru.practicum.shareit.user.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
class UserControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    UserService userService;

    @Test
    @DisplayName("Проверяем контроллер создания пользователя")
    void testCreate() throws Exception {
        final UserDto userDto = new UserDto();
        when(userService.create(any(UserDto.class))).thenReturn(userDto);
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Polina\", \"email\": \"polina@gmail.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto.getId()));
        verify(userService, times(1)).create(any(UserDto.class));
    }

    @Test
    @DisplayName("Проверяем контроллер обновления пользователя")
    void testUpdate() throws Exception {
        final UserDto userDto = new UserDto();
        when(userService.update(anyLong(), any(UserDto.class))).thenReturn(userDto);
        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Vasilek\", \"email\": \"vasilek@outlook.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto.getId()));
        verify(userService, times(1)).update(anyLong(), any(UserDto.class));
    }

    @Test
    @DisplayName("Проверяем контроллер получения пользователя")
    void testGetById() throws Exception {
        final UserDto userDto = new UserDto();
        when(userService.findUser(anyLong())).thenReturn(userDto);
        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto.getId()));
        verify(userService, times(1)).findUser(anyLong());
    }

    @Test
    @DisplayName("Проверяем контроллер получения всех пользователей")
    void testGetAll() throws Exception {
        final List<User> users = List.of(new User());
        when(userService.getUsers()).thenReturn(users);
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
        verify(userService, times(1)).getUsers();
    }

    @Test
    @DisplayName("Проверяем контроллер удаления пользователя")
    void testDelete() throws Exception {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());

        verify(userService, times(1)).delete(anyLong());
    }
}