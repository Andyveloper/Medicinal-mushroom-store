package com.setas.setas_backend.domain.port.in.user;

import com.setas.setas_backend.domain.model.User;

import java.util.Optional;

public interface ILoginUser {
    Optional<User> execute(String email, String password);
}
