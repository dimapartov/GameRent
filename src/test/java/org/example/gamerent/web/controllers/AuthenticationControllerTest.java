package org.example.gamerent.web.controllers;

import org.example.gamerent.repos.UserRepository;
import org.example.gamerent.services.impl.security.RegistrationService;
import org.example.gamerent.web.viewmodels.user_input.RegistrationInputModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(AuthenticationController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RegistrationService registrationService;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    void getLoginPage() throws Exception {
        mockMvc.perform(get("/user/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("user-login-page"));
    }

    @Test
    void getRegisterPage() throws Exception {
        mockMvc.perform(get("/user/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("user-registration-page"));
    }

    @Test
    void postLoginError() throws Exception {
        mockMvc.perform(post("/user/login-error").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/login"))
                .andExpect(flash().attribute("badCredentials", true));
    }

    @Test
    void postRegister_success() throws Exception {
        willDoNothing().given(registrationService).registerUser(any(RegistrationInputModel.class));

        mockMvc.perform(post("/user/register")
                        .param("username", "joe")
                        .param("password", "secret123")
                        .param("email", "joe@example.com")
                        .param("firstName", "Joe")
                        .param("lastName", "Doe")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/login"));

        then(registrationService).should().registerUser(any(RegistrationInputModel.class));
    }

    @Test
    void postRegister_withValidationErrors() throws Exception {
        mockMvc.perform(post("/user/register")
                        .param("username", "")
                        .param("password", "")
                        .param("email", "not-an-email")
                        .param("firstName", "")
                        .param("lastName", "")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/register"))
                .andExpect(flash().attributeExists("newUser"))
                .andExpect(flash().attributeExists("org.springframework.validation.BindingResult.newUser"));
    }

}