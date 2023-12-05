package oleg.turyk.test.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import oleg.turyk.test.dto.user.UserLoginRequestDto;
import oleg.turyk.test.dto.user.UserLoginResponseDto;
import oleg.turyk.test.dto.user.UserRegistrationRequestDto;
import oleg.turyk.test.dto.user.UserRegistrationResponseDto;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "classpath:database/insert/add-user.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:database/delete/delete-user.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class UserControllerTest {
    protected static MockMvc mockMvc;
    private static final Long ID = 2L;
    private static final String AUTH_ENDPOINT = "/auth";
    private static final String PASSWORD = "password123";
    private static final String EMAIL = "bob@example.com";
    private static final String FIRST_NAME = "Bob";
    private static final String LAST_NAME = "Smith";
    private static final String ADMIN = "ADMIN";
    private static final UserLoginRequestDto USER_LOGIN_REQUEST_DTO
            = new UserLoginRequestDto(EMAIL, PASSWORD);
    private static final UserRegistrationRequestDto USER_REGISTRATION_REQUEST_DTO
            = new UserRegistrationRequestDto();
    private static final UserRegistrationResponseDto USER_REGISTRATION_RESPONSE_DTO
            = new UserRegistrationResponseDto(ID, EMAIL, FIRST_NAME, LAST_NAME);
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
        USER_REGISTRATION_REQUEST_DTO.setEmail(EMAIL);
        USER_REGISTRATION_REQUEST_DTO.setFirstName(FIRST_NAME);
        USER_REGISTRATION_REQUEST_DTO.setLastName(LAST_NAME);
        USER_REGISTRATION_REQUEST_DTO.setPassword(PASSWORD);
        USER_REGISTRATION_REQUEST_DTO.setRepeatPassword(PASSWORD);
    }

    @Test
    @DisplayName("Login user")
    void loginUser_validCredentials_returnBearerToken() throws Exception {
        String jsonRequest = objectMapper.writeValueAsString(USER_LOGIN_REQUEST_DTO);
        MvcResult result = mockMvc.perform(post(AUTH_ENDPOINT + "/login")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(USER_LOGIN_REQUEST_DTO)))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();
        UserLoginResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                UserLoginResponseDto.class);
        assertNotNull(actual);
    }

    @Test
    @DisplayName("Register user")
    @WithMockUser(username = ADMIN, roles = {ADMIN})
    public void register_validRequest_returnRegisteredUser() throws Exception {
        UserRegistrationRequestDto newUser = new UserRegistrationRequestDto();
        newUser.setEmail("user@example.com");
        newUser.setFirstName("user");
        newUser.setLastName("user");
        newUser.setPassword("password");
        newUser.setRepeatPassword("password");
        UserRegistrationResponseDto expected
                = new UserRegistrationResponseDto(3L,
                newUser.getEmail(),
                newUser.getFirstName(),
                newUser.getLastName());

        String jsonRequest = objectMapper.writeValueAsString(newUser);
        MvcResult result = mockMvc.perform(post(AUTH_ENDPOINT + "/register")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();
        UserRegistrationResponseDto actual = objectMapper
                .readValue(result.getResponse().getContentAsString(),
                UserRegistrationResponseDto.class);
        boolean isEquals = EqualsBuilder.reflectionEquals(
                expected, actual, "id");
        assertTrue(isEquals);
    }

    @Test
    @DisplayName("Delete user")
    @WithMockUser(username = ADMIN, roles = {ADMIN})
    public void deleteUser_validId() throws Exception {
        mockMvc.perform(delete(AUTH_ENDPOINT + "/" + ID))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = ADMIN, roles = {ADMIN})
    @DisplayName("Get all users")
    void getUsers_getAllUsers() throws Exception {
        List<UserRegistrationResponseDto> expected = List.of(
                new UserRegistrationResponseDto(1L, "admin@example.com", "Oleg", "Turyk"),
                USER_REGISTRATION_RESPONSE_DTO);
        MvcResult result = mockMvc.perform(
                        get(AUTH_ENDPOINT)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        List<UserRegistrationResponseDto> actual = List.of(objectMapper.readValue(result
                .getResponse().getContentAsString(), UserRegistrationResponseDto[].class));
        assertThat(actual).usingRecursiveComparison().ignoringFields("id").isEqualTo(expected);
    }
}
