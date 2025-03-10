package ru.practicum.shareit.item.controller;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentFullDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemFullDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
@FieldDefaults(level = AccessLevel.PRIVATE)
class ItemControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ItemService itemService;

    static final String HEADER = "X-Sharer-User-Id";

    @Test
    @DisplayName("Проверяем контроллер создания предмета")
    void create() throws Exception {

        final ItemDto itemDto = new ItemDto();

        when(itemService.create(anyLong(), any(ItemDto.class))).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HEADER, 1L)
                        .content("{\"name\": \"Vasilek\", \"description\": \"lovingKitty\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()));

        verify(itemService, times(1)).create(anyLong(), any(ItemDto.class));
    }


    @Test
    @DisplayName("Проверяем контроллер по нахождению предмета по id")
    void getById() throws Exception {

        final ItemFullDto itemInfoDto = new ItemFullDto();

        when(itemService.findItem(anyLong())).thenReturn(itemInfoDto);

        mockMvc.perform(get("/items/1")
                        .header(HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemInfoDto.getId()));

        verify(itemService, times(1)).findItem(anyLong());
    }


    @Test
    @DisplayName("Проверяем контроллер по обновлению предмета")
    void update() throws Exception {

        final ItemDto itemDto = new ItemDto();

        when(itemService.update(anyLong(), anyLong(), any(ItemDto.class))).thenReturn(itemDto);

        mockMvc.perform(patch("/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HEADER, 1L)
                        .content("{\"name\": \"Updated item\", \"description\": \"Updated description\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()));

        verify(itemService, times(1)).update(anyLong(), anyLong(), any(ItemDto.class));
    }

    @Test
    @DisplayName("Проверяем контроллер по получению предметов по собственнику")
    void testGetOwnerItems() throws Exception {

        final List<ItemFullDto> items = List.of(new ItemFullDto());

        when(itemService.getUserItems(anyLong())).thenReturn(items);

        mockMvc.perform(get("/items")
                        .header(HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(itemService, times(1)).getUserItems(anyLong());
    }


    @Test
    @DisplayName("Проверяем контроллер по поиску предметов")
    void search() throws Exception {

        final List<ItemDto> items = List.of(new ItemDto());

        when(itemService.searchItemsByText(anyString())).thenReturn(items);

        mockMvc.perform(get("/items/search")
                        .param("text", "Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(itemService, times(1)).searchItemsByText(anyString());
    }

    @Test
    @DisplayName("Проверяем контроллер по добавлению комментариев")
    void addComments() throws Exception {
        final CommentFullDto commentInfoDto = new CommentFullDto();
        commentInfoDto.setText("Text");

        when(itemService.addComments(anyLong(), any(CommentDto.class), anyLong())).thenReturn(commentInfoDto);

        mockMvc.perform(post("/items/1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HEADER, 1)
                        .content("{\"text\": \"Text\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Text"));

        verify(itemService, times(1)).addComments(anyLong(), any(CommentDto.class), anyLong());
    }
}