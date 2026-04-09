package com.kanbanapp.service;

import com.kanbanapp.entity.User;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.mindrot.jbcrypt.BCrypt;

import java.time.Duration;
import java.util.Set;

@ApplicationScoped
public class AuthService {

    @Transactional
    public User register(String name, String email, String password) {
        if (User.findByEmail(email) != null) {
            throw new IllegalArgumentException("E-mail já cadastrado");
        }

        User user = new User();
        user.name = name;
        user.email = email;
        user.passwordHash = BCrypt.hashpw(password, BCrypt.gensalt());
        user.persist();
        return user;
    }

    public String login(String email, String password) {
        User user = User.findByEmail(email);
        if (user == null || !BCrypt.checkpw(password, user.passwordHash)) {
            throw new IllegalArgumentException("Credenciais inválidas");
        }
        return generateToken(user);
    }

    private String generateToken(User user) {
        return Jwt.issuer("https://kanbanapp.com")
                .subject(String.valueOf(user.id))
                .groups(Set.of("user"))
                .claim("name", user.name)
                .claim("email", user.email)
                .expiresIn(Duration.ofHours(8))
                .sign();
    }
}