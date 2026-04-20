package com.setas.setas_backend.application.usecase.user;

import com.setas.setas_backend.domain.port.in.user.IDeleteUser;
import com.setas.setas_backend.domain.port.out.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteUser implements IDeleteUser {

    private final IUserRepository userRepository;

    @Override
    public void execute(Long id) {
        userRepository.deleteById(id);
    }
}
