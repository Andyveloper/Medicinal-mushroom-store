package com.setas.setas_backend.application.usecase.user;

import com.setas.setas_backend.domain.model.User;
import com.setas.setas_backend.domain.port.in.user.IFindByEmailUser;
import com.setas.setas_backend.domain.port.in.user.ILoginUser;
import com.setas.setas_backend.domain.port.out.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoginUser implements ILoginUser {

    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Optional<User> execute(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);

        if (user.isPresent() && passwordEncoder.matches(password, user.get().getPassword())) {
            return user;
        }
        return Optional.empty();
    }
}
