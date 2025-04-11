package org.example.gamerent.services.impl;

import org.example.gamerent.models.Offer;
import org.example.gamerent.models.Rental;
import org.example.gamerent.models.Review;
import org.example.gamerent.models.User;
import org.example.gamerent.models.consts.RentalStatus;
import org.example.gamerent.repos.OfferRepository;
import org.example.gamerent.repos.RentalRepository;
import org.example.gamerent.repos.ReviewRepository;
import org.example.gamerent.repos.UserRepository;
import org.example.gamerent.services.UserService;
import org.example.gamerent.web.viewmodels.OfferViewModel;
import org.example.gamerent.web.viewmodels.RentalViewModel;
import org.example.gamerent.web.viewmodels.ReviewViewModel;
import org.example.gamerent.web.viewmodels.UserViewModel;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final OfferRepository offerRepository;
    private final RentalRepository rentalRepository;
    private final ReviewRepository reviewRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           OfferRepository offerRepository,
                           RentalRepository rentalRepository,
                           ReviewRepository reviewRepository,
                           ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.offerRepository = offerRepository;
        this.rentalRepository = rentalRepository;
        this.reviewRepository = reviewRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public UserViewModel createUser(UserViewModel userVM) {
        User user = modelMapper.map(userVM, User.class);
        user = userRepository.save(user);
        return modelMapper.map(user, UserViewModel.class);
    }

    @Override
    public UserViewModel getUserById(Long id) {
        User user = userRepository.findById(id).orElse(null);
        return user != null ? modelMapper.map(user, UserViewModel.class) : null;
    }

    @Override
    public List<UserViewModel> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserViewModel> result = new ArrayList<>();
        for (User user : users) {
            result.add(modelMapper.map(user, UserViewModel.class));
        }
        return result;
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public List<OfferViewModel> getOffersByUser(Long userId) {
        List<Offer> offers = offerRepository.findAll();
        List<OfferViewModel> result = new ArrayList<>();
        for (Offer offer : offers) {
            if (offer.getOwner() != null && offer.getOwner().getId().equals(userId)) {
                result.add(modelMapper.map(offer, OfferViewModel.class));
            }
        }
        return result;
    }

    @Override
    public List<RentalViewModel> getRentalsByUser(Long userId) {
        List<Rental> rentals = rentalRepository.findByRenter_IdAndStatus(userId, RentalStatus.ACTIVE);
        List<RentalViewModel> result = new ArrayList<>();
        for (Rental rental : rentals) {
            result.add(modelMapper.map(rental, RentalViewModel.class));
        }
        return result;
    }

    @Override
    public List<ReviewViewModel> getReviewsGivenByUser(Long userId) {
        List<Review> reviews = reviewRepository.findByReviewer_Id(userId);
        List<ReviewViewModel> result = new ArrayList<>();
        for (Review review : reviews) {
            result.add(modelMapper.map(review, ReviewViewModel.class));
        }
        return result;
    }

    @Override
    public List<ReviewViewModel> getReviewsReceivedByUser(Long userId) {
        List<Review> reviews = reviewRepository.findByReviewee_Id(userId);
        List<ReviewViewModel> result = new ArrayList<>();
        for (Review review : reviews) {
            result.add(modelMapper.map(review, ReviewViewModel.class));
        }
        return result;
    }
}