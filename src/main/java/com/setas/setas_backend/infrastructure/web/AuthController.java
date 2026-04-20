package com.setas.setas_backend.infrastructure.web;

import com.setas.setas_backend.config.JwtService;
import com.setas.setas_backend.domain.model.User;
import com.setas.setas_backend.domain.port.in.user.IDeleteUser;
import com.setas.setas_backend.domain.port.in.user.IFindByEmailUser;
import com.setas.setas_backend.domain.port.in.user.ILoginUser;
import com.setas.setas_backend.domain.port.in.user.IRegisterUser;
import com.setas.setas_backend.infrastructure.web.dto.AuthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IRegisterUser registerUser;
    private final IDeleteUser deleteUser;
    private final IFindByEmailUser findByEmailUser;
    private final ILoginUser loginUser;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody User user) {
        User created = registerUser.execute(user);
        String token = jwtService.generateToken(created.getEmail(), created.getRole().name());
        return ResponseEntity.status(HttpStatus.CREATED).body(new AuthResponse(token));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deleteUser.execute(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/{email}")
    public ResponseEntity<User> findByEmail(@PathVariable String email) {
        return findByEmailUser.execute(email).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody User user) {
        Optional<User> loggedUser = loginUser.execute(user.getEmail(), user.getPassword());
        if (loggedUser.isPresent()) {
            String token = jwtService.generateToken(loggedUser.get().getEmail(), loggedUser.get().getRole().name());
            return ResponseEntity.ok(new AuthResponse(token));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

}
