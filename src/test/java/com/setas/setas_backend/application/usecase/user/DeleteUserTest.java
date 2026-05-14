package com.setas.setas_backend.application.usecase.user;

import com.setas.setas_backend.domain.port.out.IUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DeleteUserTest {

    @Mock
    private IUserRepository userRepository;

    @InjectMocks
    private DeleteUser deleteUser;

    @Test
    void execute_deberiaDelegarElBorradoAlRepositorio() {
        deleteUser.execute(1L);

        verify(userRepository).deleteById(1L);
    }
}
