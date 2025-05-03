package org.example.gamerent.web.viewmodels.user_input;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.example.gamerent.util.validation.email.UniqueEmail;
import org.example.gamerent.util.validation.username.UniqueUsername;


public class RegistrationInputModel {

    @UniqueUsername
    @NotBlank(message = "Введите имя пользователя")
    private String username;

    @NotBlank(message = "Введите пароль")
    private String password;

    @UniqueEmail
    @NotBlank(message = "Введите почту")
    @Email(message = "Введите верную почту")
    private String email;

    @NotBlank(message = "Введите имя")
    private String firstName;

    @NotBlank(message = "Введите фамилию")
    private String lastName;


    public RegistrationInputModel() {
    }

    public RegistrationInputModel(String username, String password, String email, String firstName, String lastName) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
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