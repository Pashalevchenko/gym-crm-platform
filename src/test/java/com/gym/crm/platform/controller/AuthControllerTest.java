package com.gym.crm.platform.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.crm.platform.facade.GymAppFacade;
import com.gym.crm.platform.openapi.LoginChangeRequest;
import com.gym.crm.platform.openapi.LoginRequest;
import com.gym.crm.platform.security.GymUserDetailsService;
import com.gym.crm.platform.security.JwtService;
import com.gym.crm.platform.security.TokenBlacklistService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    private static final String USERNAME = "test.user";
    private static final String PASSWORD = "password";
    private static final String NEW_PASSWORD = "newPassword";
    private static final String BASE_URL = "/api/v1/auth";

    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GymAppFacade facade;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private GymUserDetailsService gymUserDetailsService;

    @MockitoBean
    private TokenBlacklistService tokenBlacklistService;

    @Test
    void login_shouldReturnOk() throws Exception {
        LoginRequest request = new LoginRequest()
                .username(USERNAME)
                .password(PASSWORD);

        mockMvc.perform(post(BASE_URL + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk());
        verify(facade).login(USERNAME, PASSWORD);
    }

    @Test
    void logout_shouldReturnOk() throws Exception {
        String authorizationHeader = "Bearer jwt-token";

        mockMvc.perform(post(BASE_URL + "/logout")
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader))
                .andExpect(status().isOk());

        verify(facade).logout(authorizationHeader);
    }

    @Test
    void changePassword_shouldReturnOk() throws Exception {
        LoginChangeRequest request = new LoginChangeRequest()
                .username(USERNAME)
                .oldPassword(PASSWORD)
                .newPassword(NEW_PASSWORD);

        mockMvc.perform(put(BASE_URL + "/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(facade).changePassword(any(LoginChangeRequest.class));
    }
}