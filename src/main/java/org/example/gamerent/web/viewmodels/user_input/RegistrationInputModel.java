package org.example.gamerent.web.viewmodels.user_input;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;


public class RegistrationInputModel {

    @NotBlank(message = "Введите имя пользователя")
    private String username;

    @NotBlank(message = "Введите пароль")
    private String password;

    @NotBlank(message = "Введите почту")
    @Email(message = "Введите верную почту")
    private String email;

    @NotBlank(message = "Введите имя")
    private String firstName;

    @NotBlank(message = "Введите фамилию")
    private String lastName;


    public RegistrationInputModel() {}


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