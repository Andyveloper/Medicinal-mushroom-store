package com.setas.setas_backend.application.usecase.user;

import com.setas.setas_backend.domain.model.Role;
import com.setas.setas_backend.domain.model.User;
import com.setas.setas_backend.domain.port.in.user.IRegisterUser;
import com.setas.setas_backend.domain.port.out.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegisterUser implements IRegisterUser {

    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User execute(User user) {

        if (user.getActive() == null) {
            user.setActive(true);
        }

        if (user.getRole() == null) {
            user.setRole(Role.CLIENT);
        }

        String encryptedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encryptedPassword);

        return userRepository.save(user);
    }
}
