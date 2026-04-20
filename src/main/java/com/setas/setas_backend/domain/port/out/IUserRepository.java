package com.setas.setas_backend.domain.port.out;

import com.setas.setas_backend.domain.model.User;

import java.util.Optional;

public interface IUserRepository {
    User save(User user);
    void deleteById(Long id);
    Optional<User> findByEmail(String email);
}
