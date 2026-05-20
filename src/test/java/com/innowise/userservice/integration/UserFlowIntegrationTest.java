package com.innowise.userservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innowise.userservice.dto.UserRequestDto;
import com.innowise.userservice.dto.UserResponseDto;
import com.innowise.userservice.model.User;
import com.innowise.userservice.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserFlowIntegrationTest extends BaseIntegrationTest{

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    private ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

    @Autowired
    private CacheManager cacheManager;

    @Test
    void userFullFlow_ShouldCreateVerifyInDbAndCacheInRedis() throws Exception {
        UserRequestDto requestDto = new UserRequestDto();
        requestDto.setName("Evgeniy");
        requestDto.setSurname("Innowise");
        requestDto.setBirthDate(LocalDate.of(2000, 1, 1));
        requestDto.setEmail("evgeniy@example.com");
        requestDto.setActive(true);

        String responseContent = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").value("evgeniy@example.com"))
                .andReturn().getResponse().getContentAsString();

        UserResponseDto createdUserDto = objectMapper.readValue(responseContent, UserResponseDto.class);
        Long userId = createdUserDto.getId();

        User dbUser = userRepository.findById(userId).orElse(null);
        assertNotNull(dbUser);
        assertEquals("Evgeniy", dbUser.getName());

        assertNotNull(dbUser.getCreatedAt());

        assertNull(cacheManager.getCache("users").get(userId));

        mockMvc.perform(get("/api/users/" + userId))
                .andExpect(status().isOk());

        assertNotNull(cacheManager.getCache("users").get(userId));
    }

    @Test
    void createUser_WithInvalidEmail_ShouldReturn400BadRequest() throws Exception {
        UserRequestDto invalidDto = new UserRequestDto();
        invalidDto.setName("Ivan");
        invalidDto.setSurname("Ivanov");
        invalidDto.setBirthDate(LocalDate.of(1995, 5, 5));
        invalidDto.setEmail("not-an-email-format");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").exists());
    }
}
