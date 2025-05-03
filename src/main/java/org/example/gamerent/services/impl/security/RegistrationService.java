package org.example.gamerent.services.impl.security;

import org.example.gamerent.models.User;
import org.example.gamerent.repos.UserRepository;
import org.example.gamerent.web.viewmodels.user_input.RegistrationInputModel;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class RegistrationService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;


    @Autowired
    public RegistrationService(UserRepository userRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }


    public void registerUser(RegistrationInputModel newUser) {
        Optional<User> userByUsername = userRepository.findUserByUsername(newUser.getUsername());
        if (userByUsername.isPresent()) {
            throw new RuntimeException("Имя пользователя уже занято!");
        }
        User newUserModel = modelMapper.map(newUser, User.class);
        newUserModel.setActive(true);
        newUserModel.setPassword(passwordEncoder.encode(newUser.getPassword()));
        userRepository.save(newUserModel);
    }
}