package com.setas.setas_backend.application.usecase.user;

import com.setas.setas_backend.domain.model.Role;
import com.setas.setas_backend.domain.model.User;
import com.setas.setas_backend.domain.port.out.IUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginUserTest {

    @Mock private IUserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private LoginUser loginUser;

    private final User storedUser = User.builder()
            .id(1L).name("Andy").lastname("Dev")
            .email("andy@test.com").password("$2a$hash")
            .role(Role.CLIENT).build();

    @Test
    void execute_deberiaRetornarUsuarioCuandoCredencialesSonCorrectas() {
        when(userRepository.findByEmail("andy@test.com")).thenReturn(Optional.of(storedUser));
        when(passwordEncoder.matches("plaintext", "$2a$hash")).thenReturn(true);

        Optional<User> result = loginUser.execute("andy@test.com", "plaintext");

        assertThat(result).isPresent().contains(storedUser);
    }

    @Test
    void execute_deberiaRetornarVacioCuandoUsuarioNoExiste() {
        when(userRepository.findByEmail("noexiste@test.com")).thenReturn(Optional.empty());

        Optional<User> result = loginUser.execute("noexiste@test.com", "cualquiera");

        assertThat(result).isEmpty();
    }

    @Test
    void execute_deberiaRetornarVacioCuandoContrasenaEsIncorrecta() {
        when(userRepository.findByEmail("andy@test.com")).thenReturn(Optional.of(storedUser));
        when(passwordEncoder.matches("wrong", "$2a$hash")).thenReturn(false);

        Optional<User> result = loginUser.execute("andy@test.com", "wrong");

        assertThat(result).isEmpty();
    }
}
