package org.example.gamerent.models;

import jakarta.persistence.*;
import org.example.gamerent.models.base.IdCreatedModified;

import java.util.Set;

@Entity
@Table(name = "users")
public class User extends IdCreatedModified {

    private String username;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private boolean isActive;
    private Set<Offer> offers;


    protected User() {}


    @Column(name = "username", unique = true)
    public String getUsername() {
        return username;
    }

    @Column(name = "email", unique = true)
    public String getEmail() {
        return email;
    }

    @Column(name = "password")
    public String getPassword() {
        return password;
    }

    @Column(name = "first_name")
    public String getFirstName() {
        return firstName;
    }

    @Column(name = "last_name")
    public String getLastName() {
        return lastName;
    }

    @Column(name = "is_active")
    public boolean isActive() {
        return isActive;
    }

    @OneToMany(mappedBy = "owner")
    public Set<Offer> getOffers() {
        return offers;
    }


    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public void setOffers(Set<Offer> offers) {
        this.offers = offers;
    }

}