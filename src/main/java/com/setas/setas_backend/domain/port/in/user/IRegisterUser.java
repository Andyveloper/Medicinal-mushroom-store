package com.setas.setas_backend.domain.port.in.user;

import com.setas.setas_backend.domain.model.User;

public interface IRegisterUser {
    User execute(User user);
}
