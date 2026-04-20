package com.setas.setas_backend.application.usecase.user;

import com.setas.setas_backend.domain.model.User;
import com.setas.setas_backend.domain.port.in.user.IFindByEmailUser;
import com.setas.setas_backend.domain.port.out.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FindByEmailUser implements IFindByEmailUser {

    private final IUserRepository userRepository;

    @Override
    public Optional<User> execute(String email) {
        return userRepository.findByEmail(email);
    }
}
