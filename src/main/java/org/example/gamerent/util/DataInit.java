package org.example.gamerent.util;

import com.github.javafaker.Faker;
import jakarta.persistence.EntityManager;
import org.example.gamerent.models.Offer;
import org.example.gamerent.models.Review;
import org.example.gamerent.models.User;
import org.example.gamerent.repos.OfferRepository;
import org.example.gamerent.repos.RentalRepository;
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
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Component
public class DataInit implements CommandLineRunner {

    private final BrandService brandService;
    private final OfferService offerService;
    private final RentalService rentalService;
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
                    EntityManager entityManager,
                    RentalService rentalService) {
        this.brandService = brandService;
        this.offerService = offerService;
        this.registrationService = registrationService;
        this.userRepository = userRepository;
        this.reviewRepository = reviewRepository;
        this.offerRepository = offerRepository;
        this.rentalRepository = rentalRepository;
        this.entityManager = entityManager;
        this.rentalService = rentalService;
    }

    @Transactional
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
            System.out.println("Приложение готово к работе");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void seedUsers() {
        RegistrationInputModel firstUser = new RegistrationInputModel();
        firstUser.setUsername("anton");
        firstUser.setPassword("anton");
        firstUser.setEmail("anton@example.com");
        firstUser.setFirstName("Антон");
        firstUser.setLastName("Партов");
        registrationService.registerUser(firstUser);
        RegistrationInputModel secondUser = new RegistrationInputModel();
        secondUser.setUsername("gena");
        secondUser.setPassword("gena");
        secondUser.setEmail("gena@example.com");
        secondUser.setFirstName("Гена");
        secondUser.setLastName("Горин");
        registrationService.registerUser(secondUser);
    }

    private void seedBrands() {
        Set<String> usedNames = new HashSet<>();
        int uniqueBrandsAmount = 10;
        while (usedNames.size() < uniqueBrandsAmount) {
            String brandName = faker.company().name();
            if (usedNames.add(brandName)) {
                BrandCreationInputModel brandModel = new BrandCreationInputModel();
                brandModel.setName(brandName);
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
        for (int i = 0; i < 250; i++) {
            OfferCreationInputModel model = createRandomOfferModel(brandNames);
            String owner = usernames.get(0);
            offerService.seedOffer(model, owner);
        }
        for (int i = 0; i < 250; i++) {
            OfferCreationInputModel model = createRandomOfferModel(brandNames);
            String owner = usernames.get(1);
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

    @Transactional
    public void seedRentals() {
        List<String> usernames = userRepository.findAll().stream()
                .map(User::getUsername)
                .collect(Collectors.toList());
        List<Offer> allOffers = offerRepository.findAll();
        final int perUser = 250;
        for (String username : usernames) {
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(username, null);
            SecurityContextHolder.getContext().setAuthentication(auth);
            List<Offer> otherOffers = allOffers.stream()
                    .filter(o -> !o.getOwner().getUsername().equals(username))
                    .collect(Collectors.toList());
            Collections.shuffle(otherOffers, random);
            for (int i = 0; i < perUser; i++) {
                Offer offer = otherOffers.get(i % otherOffers.size());
                int min = offer.getMinRentalDays();
                int max = offer.getMaxRentalDays();
                int days = random.nextInt(max - min + 1) + min;
                RentalRequestInputModel req = new RentalRequestInputModel();
                req.setOfferId(offer.getId());
                req.setDays(days);
                rentalService.createRentalRequest(req);
            }
        }
        SecurityContextHolder.clearContext();
    }

}