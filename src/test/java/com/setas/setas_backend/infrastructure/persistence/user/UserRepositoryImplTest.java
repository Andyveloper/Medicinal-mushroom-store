package com.setas.setas_backend.infrastructure.persistence.user;

import com.setas.setas_backend.domain.model.Role;
import com.setas.setas_backend.domain.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRepositoryImplTest {

    @Mock private JpaUserRepository jpaUserRepository;

    @InjectMocks
    private UserRepositoryImpl userRepositoryImpl;

    private final User user = User.builder().id(1L).name("Andy").lastname("Dev").email("andy@test.com").role(Role.CLIENT).build();

    @Test
    void save_shouldDelegateToJpaRepository() {
        when(jpaUserRepository.save(user)).thenReturn(user);

        User result = userRepositoryImpl.save(user);

        assertThat(result).isEqualTo(user);
        verify(jpaUserRepository).save(user);
    }

    @Test
    void deleteById_shouldDelegateToJpaRepository() {
        userRepositoryImpl.deleteById(1L);

        verify(jpaUserRepository).deleteById(1L);
    }

    @Test
    void findByEmail_shouldReturnUserWhenExists() {
        when(jpaUserRepository.findByEmail("andy@test.com")).thenReturn(Optional.of(user));

        Optional<User> result = userRepositoryImpl.findByEmail("andy@test.com");

        assertThat(result).isPresent().contains(user);
    }
}
