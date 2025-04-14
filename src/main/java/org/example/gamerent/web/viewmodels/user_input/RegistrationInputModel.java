package org.example.gamerent.web.viewmodels.user_input;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.example.gamerent.util.validation.email.UniqueEmail;
import org.example.gamerent.util.validation.username.UniqueUsername;


public class RegistrationInputModel {

    @UniqueUsername
    @NotNull(message = "Введите имя пользователя")
    @NotEmpty(message = "Введите имя пользователя")
    @NotBlank(message = "Введите имя пользователя")
    private String username;

    @NotNull(message = "Введите пароль")
    @NotEmpty(message = "Введите пароль")
    @NotBlank(message = "Введите пароль")
    private String password;

    @UniqueEmail
    @NotNull(message = "Введите почту")
    @NotEmpty(message = "Введите почту")
    @NotBlank(message = "Введите почту")
    @Email(message = "Введите верную почту")
    private String email;

    @NotNull(message = "Введите имя")
    @NotEmpty(message = "Введите имя")
    @NotBlank(message = "Введите имя")
    private String firstName;

    @NotNull(message = "Введите фамилию")
    @NotEmpty(message = "Введите фамилию")
    @NotBlank(message = "Введите фамилию")
    private String lastName;


    public RegistrationInputModel() {
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

}