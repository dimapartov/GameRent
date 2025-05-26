package org.example.gamerent.services.impl;

import org.example.gamerent.models.User;
import org.example.gamerent.repos.UserRepository;
import org.example.gamerent.services.impl.security.CustomUserDetailsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    @Test
    void loadUserByUsername_userExists_shouldReturnDetails() {
        User user = new User();
        user.setUsername("john");
        user.setPassword("pwd");
        user.setActive(true);
        when(userRepository.findUserByUsername("john")).thenReturn(Optional.of(user));

        UserDetails details = userDetailsService.loadUserByUsername("john");

        assertEquals("john", details.getUsername());
        assertEquals("pwd", details.getPassword());
        assertTrue(details.isEnabled());
    }

    @Test
    void loadUserByUsername_userNotFound_shouldThrow() {
        when(userRepository.findUserByUsername("jane")).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername("jane"));
    }

}