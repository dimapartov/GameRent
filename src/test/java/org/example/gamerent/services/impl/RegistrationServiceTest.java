package org.example.gamerent.services.impl;

import org.example.gamerent.models.User;
import org.example.gamerent.repos.UserRepository;
import org.example.gamerent.services.impl.security.RegistrationService;
import org.example.gamerent.web.viewmodels.user_input.RegistrationInputModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private RegistrationService registrationService;

    private RegistrationInputModel input;
    private User userEntity;

    @BeforeEach
    void setUp() {
        input = new RegistrationInputModel();
        input.setUsername("testuser");
        input.setPassword("password");

        userEntity = new User();
        userEntity.setUsername("testuser");
        userEntity.setActive(true);
    }

    @Test
    void registerUser_success() {
        when(userRepository.findUserByUsername("testuser")).thenReturn(Optional.empty());
        when(modelMapper.map(input, User.class)).thenReturn(userEntity);
        when(passwordEncoder.encode("password")).thenReturn("encodedPass");

        registrationService.registerUser(input);

        assertTrue(userEntity.isActive());
        assertEquals("encodedPass", userEntity.getPassword());
        verify(userRepository, times(1)).save(userEntity);
    }

    @Test
    void registerUser_usernameTaken_shouldThrow() {
        when(userRepository.findUserByUsername("testuser")).thenReturn(Optional.of(new User()));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> registrationService.registerUser(input));
        assertEquals("Имя пользователя уже занято!", ex.getMessage());
    }

}