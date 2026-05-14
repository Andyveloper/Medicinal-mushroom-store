package com.setas.setas_backend.infrastructure.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.setas.setas_backend.config.JwtService;
import com.setas.setas_backend.domain.model.Role;
import com.setas.setas_backend.domain.model.User;
import com.setas.setas_backend.domain.port.in.user.IDeleteUser;
import com.setas.setas_backend.domain.port.in.user.IFindByEmailUser;
import com.setas.setas_backend.domain.port.in.user.ILoginUser;
import com.setas.setas_backend.domain.port.in.user.IRegisterUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock private IRegisterUser registerUser;
    @Mock private IDeleteUser deleteUser;
    @Mock private IFindByEmailUser findByEmailUser;
    @Mock private ILoginUser loginUser;
    @Mock private JwtService jwtService;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final User user = User.builder()
            .id(1L).name("Andy").lastname("Dev")
            .email("andy@test.com").role(Role.CLIENT).build();

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    void register_shouldReturn201WithToken() throws Exception {
        when(registerUser.execute(any(User.class))).thenReturn(user);
        when(jwtService.generateToken("andy@test.com", "CLIENT")).thenReturn("jwt-token");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"andy@test.com\",\"password\":\"pass\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("jwt-token"));
    }

    @Test
    void login_shouldReturn200WithTokenWhenCredentialsAreValid() throws Exception {
        when(loginUser.execute("andy@test.com", "pass")).thenReturn(Optional.of(user));
        when(jwtService.generateToken("andy@test.com", "CLIENT")).thenReturn("jwt-token");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"andy@test.com\",\"password\":\"pass\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"));
    }

    @Test
    void login_shouldReturn401WhenCredentialsAreInvalid() throws Exception {
        when(loginUser.execute("andy@test.com", "wrong")).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"andy@test.com\",\"password\":\"wrong\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void findByEmail_shouldReturn200WhenUserExists() throws Exception {
        when(findByEmailUser.execute("andy@test.com")).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/auth/andy@test.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("andy@test.com"));
    }

    @Test
    void findByEmail_shouldReturn404WhenUserNotFound() throws Exception {
        when(findByEmailUser.execute("nobody@test.com")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/auth/nobody@test.com"))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_shouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/auth/1"))
                .andExpect(status().isNoContent());

        verify(deleteUser).execute(1L);
    }
}
