package org.example.gamerent.util;

import com.github.javafaker.Faker;
import org.example.gamerent.models.Review;
import org.example.gamerent.models.User;
import org.example.gamerent.repos.ReviewRepository;
import org.example.gamerent.repos.UserRepository;
import org.example.gamerent.services.BrandService;
import org.example.gamerent.services.OfferService;
import org.example.gamerent.services.impl.security.RegistrationService;
import org.example.gamerent.web.viewmodels.user_input.BrandCreationInputModel;
import org.example.gamerent.web.viewmodels.user_input.OfferCreationInputModel;
import org.example.gamerent.web.viewmodels.user_input.RegistrationInputModel;
import org.example.gamerent.web.viewmodels.user_input.ReviewInputModel;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;


@Component
public class DataInit implements CommandLineRunner {

    private final BrandService brandService;
    private final OfferService offerService;
    private final RegistrationService registrationService;
    private final UserRepository userRepo;
    private final ReviewRepository reviewRepo;
    private final Faker faker = new Faker();
    private final Random random = new Random();
    private final ModelMapper mapper = new ModelMapper();


    @Autowired
    public DataInit(BrandService brandService, OfferService offerService, RegistrationService registrationService, UserRepository userRepo, ReviewRepository reviewRepo) {
        this.brandService = brandService;
        this.offerService = offerService;
        this.registrationService = registrationService;
        this.userRepo = userRepo;
        this.reviewRepo = reviewRepo;
    }


    @Override
    public void run(String... args) {
        try {
            seedUsers();
            seedBrands();
            seedOffers();
            seedReviews();
            System.out.println("Приложение готово к работе");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }


    private void seedUsers() {
        RegistrationInputModel user1 = new RegistrationInputModel();
        user1.setUsername("d");
        user1.setEmail("dima@dima.ru");
        user1.setPassword("d");
        user1.setFirstName("Dima");
        user1.setLastName("Partov");
        registrationService.registerUser(user1);

        RegistrationInputModel user2 = new RegistrationInputModel();
        user2.setUsername("dd");
        user2.setEmail("dima2@dima.ru");
        user2.setPassword("dd");
        user2.setFirstName("Dima");
        user2.setLastName("Kubarev");
        registrationService.registerUser(user2);

/*        for (int i = 0; i < 4; i++) {
            RegistrationInputModel randomUser = new RegistrationInputModel();
            randomUser.setUsername(faker.name().username());
            randomUser.setEmail(faker.internet().emailAddress());
            randomUser.setPassword(faker.internet().password());
            randomUser.setFirstName(faker.name().firstName());
            randomUser.setLastName(faker.name().lastName());
            registrationService.registerUser(randomUser);
        }*/

        System.out.println("Пользователи добавлены");
    }

    private void seedBrands() {
        for (int i = 0; i < 5; i++) {
            BrandCreationInputModel brandModel = new BrandCreationInputModel();
            brandModel.setName(faker.company().name());
            brandModel.setDescription(faker.lorem().sentence());
            brandModel.setPhoto("brand_logo.png");
            brandService.createBrand(brandModel);
        }
        System.out.println("Бренды добавлены");
    }

    private void seedOffers() {
        List<String> brandNames = brandService.getAllBrandsDTOs().stream()
                .map(b -> b.getName())
                .collect(Collectors.toList());

        for (int i = 0; i < 1; i++) {
            OfferCreationInputModel offerCreationInputModel = createRandomOfferModel(brandNames);
            offerCreationInputModel.setPhoto("brand_logo.png");
            offerService.seedOffer(offerCreationInputModel, "d");
        }
        System.out.println("Офферы юзера 1 добавлены");
        for (int i = 0; i < 1; i++) {
            OfferCreationInputModel model = createRandomOfferModel(brandNames);
            model.setPhoto("brand_logo.png");
            offerService.seedOffer(model, "dd");
        }
        System.out.println("Офферы юзера 2 добавлены");
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
        User u1 = userRepo.findUserByUsername("d").orElseThrow(() -> new IllegalStateException("d не найден"));
        User u2 = userRepo.findUserByUsername("dd").orElseThrow(() -> new IllegalStateException("dd не найден"));
        for (int i = 0; i < 10; i++) {
            ReviewInputModel in = new ReviewInputModel();
            in.setRevieweeUsername("dd");
            in.setRating(random.nextInt(5) + 1);
            in.setText(faker.lorem().sentence());
            Review r = mapper.map(in, Review.class);
            r.setReviewer(u1);
            r.setReviewee(u2);
            r.setCreated(LocalDateTime.now().minusDays(random.nextInt(30)));
            reviewRepo.save(r);
        }
        System.out.println("10 отзывов от d на dd добавлены");
        for (int i = 0; i < 10; i++) {
            ReviewInputModel in = new ReviewInputModel();
            in.setRevieweeUsername("d");
            in.setRating(random.nextInt(5) + 1);
            in.setText(faker.lorem().sentence());
            Review r = mapper.map(in, Review.class);
            r.setReviewer(u2);
            r.setReviewee(u1);
            r.setCreated(LocalDateTime.now().minusDays(random.nextInt(30)));
            reviewRepo.save(r);
        }
        System.out.println("10 отзывов от dd на d добавлены");
    }

}