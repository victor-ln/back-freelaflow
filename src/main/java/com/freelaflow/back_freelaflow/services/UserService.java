package com.freelaflow.back_freelaflow.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.freelaflow.back_freelaflow.models.User;
import com.freelaflow.back_freelaflow.repository.UserRepository;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public User createUser(String email, String password, String nome) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email já está em uso");
        }

        User user = new User();
        user.setEmail(email);
        user.setPwd(passwordEncoder.encode(password));
        user.setNome(nome);

        emailService.enviarCodigoVerificacao(email, nome);

        return userRepository.save(user);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}