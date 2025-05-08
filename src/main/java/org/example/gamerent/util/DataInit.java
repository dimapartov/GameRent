package org.example.gamerent.util;

import com.github.javafaker.Faker;
import org.example.gamerent.models.Offer;
import org.example.gamerent.models.Review;
import org.example.gamerent.models.User;
import org.example.gamerent.models.consts.OfferStatus;
import org.example.gamerent.repos.OfferRepository;
import org.example.gamerent.repos.ReviewRepository;
import org.example.gamerent.repos.UserRepository;
import org.example.gamerent.services.BrandService;
import org.example.gamerent.services.OfferService;
import org.example.gamerent.services.RentalService;
import org.example.gamerent.services.impl.security.RegistrationService;
import org.example.gamerent.web.viewmodels.user_input.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;


@Component
public class DataInit implements CommandLineRunner {

    private final BrandService brandService;
    private final OfferService offerService;
    private final RegistrationService registrationService;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final OfferRepository offerRepository;
    private final RentalService rentalService;
    private final Faker faker = new Faker();
    private final Random random = new Random();
    private final ModelMapper modelMapper = new ModelMapper();

    @Autowired
    public DataInit(BrandService brandService,
                    OfferService offerService,
                    RegistrationService registrationService,
                    UserRepository userRepository,
                    ReviewRepository reviewRepository,
                    OfferRepository offerRepository,
                    RentalService rentalService) {
        this.brandService = brandService;
        this.offerService = offerService;
        this.registrationService = registrationService;
        this.userRepository = userRepository;
        this.reviewRepository = reviewRepository;
        this.offerRepository = offerRepository;
        this.rentalService = rentalService;
    }

    @Override
    public void run(String... args) {
        try {
            seedUsers();
            seedBrands();
            seedOffers();
            seedReviews();
            seedRentalRequests();
        } catch (Exception e) {
        }
    }

    private void seedUsers() {
        RegistrationInputModel user1 = new RegistrationInputModel();
        user1.setUsername("a");
        user1.setEmail("dima@dima.ru");
        user1.setPassword("a");
        user1.setFirstName("Dima");
        user1.setLastName("Partov");
        registrationService.registerUser(user1);
        RegistrationInputModel user2 = new RegistrationInputModel();
        user2.setUsername("aa");
        user2.setEmail("dima2@dima.ru");
        user2.setPassword("aa");
        user2.setFirstName("Dima");
        user2.setLastName("Kubarev");
        registrationService.registerUser(user2);
//        for (int i = 0; i < 10; i++) {
//            RegistrationInputModel randomUser = new RegistrationInputModel();
//            randomUser.setUsername("a" + i);
//            randomUser.setEmail(faker.internet().emailAddress());
//            randomUser.setPassword("a" + i);
//            randomUser.setFirstName(faker.name().firstName());
//            randomUser.setLastName(faker.name().lastName());
//            registrationService.registerUser(randomUser);
//        }
    }

    private void seedBrands() {
        for (int i = 0; i < 5; i++) {
            BrandCreationInputModel brandModel = new BrandCreationInputModel();
            brandModel.setName(faker.company().name());
            brandModel.setDescription(faker.lorem().sentence());
            brandModel.setPhoto("brand_logo.png");
            brandService.createBrand(brandModel);
        }
    }

    private void seedOffers() {
        List<String> brandNames = brandService.getAllBrandsDTOs().stream()
                .map(b -> b.getName())
                .collect(Collectors.toList());
        for (int i = 0; i < 2; i++) {
            OfferCreationInputModel offerCreationInputModel = createRandomOfferModel(brandNames);
            offerService.seedOffer(offerCreationInputModel, "a");
        }
        for (int i = 0; i < 2; i++) {
            OfferCreationInputModel model = createRandomOfferModel(brandNames);
            offerService.seedOffer(model, "aa");
        }
    }

    private OfferCreationInputModel createRandomOfferModel(List<String> brandNames) {
        OfferCreationInputModel model = new OfferCreationInputModel();
        model.setDescription(faker.lorem().sentence());
        model.setPrice(BigDecimal.valueOf(random.nextInt(900) + 100));
        model.setBrand(brandNames.get(random.nextInt(brandNames.size())));
        model.setGameName(faker.name().title());
        int minDays = random.nextInt(3) + 1;
        model.setMinRentalDays(minDays);
        model.setMaxRentalDays(minDays + random.nextInt(5) + 1);
        return model;
    }

    private void seedReviews() {
        List<User> users = userRepository.findAll();
        if (users.size() < 2) {
            throw new IllegalStateException("Должно быть как минимум 2 пользователя для генерации отзывов");
        }
        for (int i = 0; i < 500; i++) {
            User reviewer = users.get(random.nextInt(users.size()));
            User reviewee;
            do {
                reviewee = users.get(random.nextInt(users.size()));
            } while (reviewee.getId().equals(reviewer.getId()));
            ReviewInputModel in = new ReviewInputModel();
            in.setRevieweeUsername(reviewee.getUsername());
            in.setRating(random.nextInt(5) + 1);
            in.setText(faker.lorem().sentence());
            Review r = modelMapper.map(in, Review.class);
            r.setReviewer(reviewer);
            r.setReviewee(reviewee);
            r.setCreated(LocalDateTime.now().minusDays(random.nextInt(30)));
            reviewRepository.save(r);
        }
    }

    private void seedRentalRequests() {
        List<Offer> offersA = offerRepository.findAllByOwnerUsername("a");
        List<Offer> offersAA = offerRepository.findAllByOwnerUsername("aa");
        UsernamePasswordAuthenticationToken authA = new UsernamePasswordAuthenticationToken("a", null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authA);
        for (int i = 0; i < 2; i++) {
            Offer offer = offersAA.get(random.nextInt(offersAA.size()));
            int days = random.nextInt(offer.getMaxRentalDays() - offer.getMinRentalDays() + 1) + offer.getMinRentalDays();
            RentalRequestInputModel input = new RentalRequestInputModel(offer.getId(), days);
            rentalService.createRentalRequest(input);
            offer.setStatus(OfferStatus.AVAILABLE);
            offerRepository.save(offer);
        }
        UsernamePasswordAuthenticationToken authAA = new UsernamePasswordAuthenticationToken("aa", null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authAA);
        for (int i = 0; i < 2; i++) {
            Offer offer = offersA.get(random.nextInt(offersA.size()));
            int days = random.nextInt(offer.getMaxRentalDays() - offer.getMinRentalDays() + 1) + offer.getMinRentalDays();
            RentalRequestInputModel input = new RentalRequestInputModel(offer.getId(), days);
            rentalService.createRentalRequest(input);
            offer.setStatus(OfferStatus.AVAILABLE);
            offerRepository.save(offer);
        }
    }

}