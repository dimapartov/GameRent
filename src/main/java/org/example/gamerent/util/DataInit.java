package org.example.gamerent.util;

import com.github.javafaker.Faker;
import jakarta.persistence.EntityManager;
import org.example.gamerent.models.Offer;
import org.example.gamerent.models.Rental;
import org.example.gamerent.models.Review;
import org.example.gamerent.models.User;
import org.example.gamerent.models.consts.RentalStatus;
import org.example.gamerent.repos.OfferRepository;
import org.example.gamerent.repos.RentalRepository;
import org.example.gamerent.repos.ReviewRepository;
import org.example.gamerent.repos.UserRepository;
import org.example.gamerent.services.BrandService;
import org.example.gamerent.services.OfferService;
import org.example.gamerent.services.impl.security.RegistrationService;
import org.example.gamerent.web.viewmodels.user_input.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;


@Component
public class DataInit implements CommandLineRunner {

    private final BrandService brandService;
    private final OfferService offerService;
    private final RegistrationService registrationService;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final OfferRepository offerRepository;
    private final RentalRepository rentalRepository;
    private final Faker faker = new Faker();
    private final Random random = new Random();
    private final ModelMapper modelMapper = new ModelMapper();
    private final EntityManager entityManager;

    @Autowired
    public DataInit(BrandService brandService,
                    OfferService offerService,
                    RegistrationService registrationService,
                    UserRepository userRepository,
                    ReviewRepository reviewRepository,
                    OfferRepository offerRepository,
                    RentalRepository rentalRepository,
                    EntityManager entityManager) {
        this.brandService = brandService;
        this.offerService = offerService;
        this.registrationService = registrationService;
        this.userRepository = userRepository;
        this.reviewRepository = reviewRepository;
        this.offerRepository = offerRepository;
        this.rentalRepository = rentalRepository;
        this.entityManager = entityManager;
    }

    @Override
    public void run(String... args) {
        try {
            seedUsers();
            System.out.println("Users seeded successfully.");
            seedBrands();
            System.out.println("Brands seeded successfully.");
            seedOffers();
            System.out.println("Offers seeded successfully.");
            seedReviews();
            System.out.println("Reviews seeded successfully.");
            seedRentals();
            System.out.println("Rentals seeded successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void seedUsers() {
        for (int i = 0; i < 2; i++) {
            RegistrationInputModel userModel = new RegistrationInputModel();
            userModel.setUsername(String.valueOf(i));
            userModel.setEmail("user" + i + "@example.com");
            userModel.setPassword(String.valueOf(i));
            userModel.setFirstName(faker.name().firstName());
            userModel.setLastName(faker.name().lastName());
            registrationService.registerUser(userModel);
        }
    }

    private void seedBrands() {
        Set<String> usedNames = new HashSet<>();
        int target = 500;
        while (usedNames.size() < target) {
            String name = faker.company().name();
            if (usedNames.add(name)) {
                BrandCreationInputModel brandModel = new BrandCreationInputModel();
                brandModel.setName(name);
                brandModel.setDescription(faker.lorem().sentence());
                brandModel.setPhoto("brand_logo.png");
                brandService.createBrand(brandModel);
            }
        }
    }

    private void seedOffers() {
        List<String> usernames = userRepository.findAll().stream()
                .map(User::getUsername)
                .collect(Collectors.toList());
        List<String> brandNames = brandService.getAllBrandsDTOs().stream()
                .map(b -> b.getName())
                .collect(Collectors.toList());
        for (int i = 0; i < 500; i++) {
            OfferCreationInputModel model = createRandomOfferModel(brandNames);
            String owner = usernames.get(random.nextInt(usernames.size()));
            offerService.seedOffer(model, owner);
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

    private void seedRentals() {
        List<Offer> offers = offerRepository.findAll();
        List<User> users = userRepository.findAll();
        RentalStatus[] statuses = RentalStatus.values();
        int total = 500;
        int perStatus = total / statuses.length;
        int extra = total % statuses.length;
        for (int s = 0; s < statuses.length; s++) {
            RentalStatus status = statuses[s];
            int count = perStatus + (s < extra ? 1 : 0);
            for (int i = 0; i < count; i++) {
                User renter = users.get(random.nextInt(users.size()));
                List<Offer> ownerOffers = offers.stream()
                        .filter(o -> !o.getOwner().getId().equals(renter.getId()))
                        .collect(Collectors.toList());
                if (ownerOffers.isEmpty()) continue;
                Offer offer = ownerOffers.get(random.nextInt(ownerOffers.size()));
                RentalRequestInputModel in = new RentalRequestInputModel(
                        offer.getId(),
                        random.nextInt(offer.getMaxRentalDays() - offer.getMinRentalDays() + 1)
                                + offer.getMinRentalDays()
                );
                Rental rental = modelMapper.map(in, Rental.class);
                rental.setId(null);
                LocalDateTime start = LocalDateTime.now().minusDays(random.nextInt(30));
                LocalDateTime end = start.plusDays(in.getDays());
                rental.setStartDate(start);
                rental.setEndDate(end);
                rental.setOffer(offer);
                rental.setRenter(renter);
                rental.setStatus(status);
                rentalRepository.save(rental);
                entityManager.detach(rental);
            }
        }
    }

}