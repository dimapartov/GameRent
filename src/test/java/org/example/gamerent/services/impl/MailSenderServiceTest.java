package org.example.gamerent.services.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class MailSenderServiceTest {

    @Mock
    private JavaMailSender javaMailSender;
    @InjectMocks
    private MailSenderService mailSenderService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(mailSenderService, "from", "noreply@example.com");
    }

    @Test
    void sendSimpleMail_shouldConstructAndSendMessage() {
        mailSenderService.sendSimpleMail("user@example.com", "Subject", "Hello");
        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
    }

}