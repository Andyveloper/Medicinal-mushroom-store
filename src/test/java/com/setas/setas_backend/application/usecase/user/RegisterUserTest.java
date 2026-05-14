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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegisterUserTest {

    @Mock private IUserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private RegisterUser registerUser;

    @Test
    void execute_deberiaEstablecerRolClienteYActivoTrueCuandoSonNull() {
        User input = new User();
        input.setName("Andy");
        input.setLastname("Dev");
        input.setEmail("andy@test.com");
        input.setPassword("plaintext");

        when(passwordEncoder.encode("plaintext")).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = registerUser.execute(input);

        assertThat(result.getRole()).isEqualTo(Role.CLIENT);
        assertThat(result.getActive()).isTrue();
        assertThat(result.getPassword()).isEqualTo("hashed");
    }

    @Test
    void execute_deberiaConservarRolAdminCuandoYaEstaEstablecido() {
        User input = new User();
        input.setName("Admin");
        input.setLastname("User");
        input.setEmail("admin@test.com");
        input.setPassword("secret");
        input.setRole(Role.ADMIN);

        when(passwordEncoder.encode("secret")).thenReturn("hashedSecret");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = registerUser.execute(input);

        assertThat(result.getRole()).isEqualTo(Role.ADMIN);
    }

    @Test
    void execute_deberiaEncriptarContrasena() {
        User input = new User();
        input.setEmail("andy@test.com");
        input.setPassword("miPassword123");

        when(passwordEncoder.encode("miPassword123")).thenReturn("$2a$hash");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = registerUser.execute(input);

        assertThat(result.getPassword()).isEqualTo("$2a$hash");
        assertThat(result.getPassword()).isNotEqualTo("miPassword123");
    }
}
