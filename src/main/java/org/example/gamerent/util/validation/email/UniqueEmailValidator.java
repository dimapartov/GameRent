package org.example.gamerent.util.validation.email;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.example.gamerent.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;


public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {

    private UserRepository userRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        return userRepository.findUserByEmail(email).isEmpty();
    }

}