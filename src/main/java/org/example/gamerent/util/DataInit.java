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
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final Faker faker = new Faker();
    private final Random random = new Random();
    private final ModelMapper modelMapper = new ModelMapper();


    @Autowired
    public DataInit(BrandService brandService, OfferService offerService, RegistrationService registrationService, UserRepository userRepository, ReviewRepository reviewRepository) {
        this.brandService = brandService;
        this.offerService = offerService;
        this.registrationService = registrationService;
        this.userRepository = userRepository;
        this.reviewRepository = reviewRepository;
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

        /*for (int i = 0; i < 10; i++) {
            RegistrationInputModel randomUser = new RegistrationInputModel();
            randomUser.setUsername("a" + i);
            randomUser.setEmail(faker.internet().emailAddress());
            randomUser.setPassword("a" + i);
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

        for (int i = 0; i < 2; i++) {
            OfferCreationInputModel offerCreationInputModel = createRandomOfferModel(brandNames);
            offerService.seedOffer(offerCreationInputModel, "a");
        }
        System.out.println("Офферы юзера 1 добавлены");
        for (int i = 0; i < 2; i++) {
            OfferCreationInputModel model = createRandomOfferModel(brandNames);
            offerService.seedOffer(model, "aa");
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
        // Получаем список всех пользователей
        List<User> users = userRepository.findAll();
        int userCount = users.size();
        if (userCount < 2) {
            throw new IllegalStateException("Должно быть как минимум 2 пользователя для генерации отзывов");
        }

        for (int i = 0; i < 50; i++) {
            // Выбираем случайного автора
            User reviewer = users.get(random.nextInt(userCount));
            // Выбираем случайного получателя, гарантируя, что это не тот же самый
            User reviewee;
            do {
                reviewee = users.get(random.nextInt(userCount));
            } while (reviewee.getId().equals(reviewer.getId()));

            // Формируем входную модель и мапим её в сущность
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
        System.out.println("500 случайных отзывов добавлены");
    }

}