package com.setas.setas_backend.application.usecase.user;

import com.setas.setas_backend.domain.model.Role;
import com.setas.setas_backend.domain.model.User;
import com.setas.setas_backend.domain.port.out.IUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindByEmailUserTest {

    @Mock
    private IUserRepository userRepository;

    @InjectMocks
    private FindByEmailUser findByEmailUser;

    private final User user = User.builder().id(1L).name("Andy").lastname("Dev").email("andy@test.com").role(Role.CLIENT).build();

    @Test
    void execute_deberiaRetornarUsuarioCuandoExiste() {
        when(userRepository.findByEmail("andy@test.com")).thenReturn(Optional.of(user));

        Optional<User> result = findByEmailUser.execute("andy@test.com");

        assertThat(result).isPresent().contains(user);
    }

    @Test
    void execute_deberiaRetornarVacioCuandoNoExiste() {
        when(userRepository.findByEmail("noexiste@test.com")).thenReturn(Optional.empty());

        Optional<User> result = findByEmailUser.execute("noexiste@test.com");

        assertThat(result).isEmpty();
    }
}
