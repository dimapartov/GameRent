package org.example.gamerent.services;

import org.example.gamerent.web.viewmodels.OfferViewModel;
import org.example.gamerent.web.viewmodels.RentalViewModel;
import org.example.gamerent.web.viewmodels.ReviewViewModel;
import org.example.gamerent.web.viewmodels.UserViewModel;

import java.util.List;

public interface UserService {
    // CRUD-методы с использованием viewmodel
    UserViewModel createUser(UserViewModel userVM);
    UserViewModel getUserById(Long id);
    List<UserViewModel> getAllUsers();
    void deleteUser(Long id);

    // Бизнес-методы (все операции с viewmodel)
    List<OfferViewModel> getOffersByUser(Long userId);
    List<RentalViewModel> getRentalsByUser(Long userId);
    List<ReviewViewModel> getReviewsGivenByUser(Long userId);
    List<ReviewViewModel> getReviewsReceivedByUser(Long userId);
}