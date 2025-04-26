package org.example.gamerent.util;

import com.github.javafaker.Faker;
import org.example.gamerent.services.BrandService;
import org.example.gamerent.services.OfferService;
import org.example.gamerent.services.impl.security.RegistrationService;
import org.example.gamerent.web.viewmodels.user_input.BrandCreationInputModel;
import org.example.gamerent.web.viewmodels.user_input.OfferCreationInputModel;
import org.example.gamerent.web.viewmodels.user_input.RegistrationInputModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;


@Component
public class DataInit implements CommandLineRunner {

    private final BrandService brandService;
    private final OfferService offerService;
    private final RegistrationService registrationService;
    private final Faker faker = new Faker();
    private final Random random = new Random();


    @Autowired
    public DataInit(BrandService brandService,
                    OfferService offerService,
                    RegistrationService registrationService) {
        this.brandService = brandService;
        this.offerService = offerService;
        this.registrationService = registrationService;
    }


    @Override
    public void run(String... args){
        try{
            seedUsers();
            seedBrands();
            seedOffers();
        } catch (Exception e){
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

        for (int i = 0; i < 4; i++) {
            RegistrationInputModel randomUser = new RegistrationInputModel();
            randomUser.setUsername(faker.name().username());
            randomUser.setEmail(faker.internet().emailAddress());
            randomUser.setPassword(faker.internet().password());
            randomUser.setFirstName(faker.name().firstName());
            randomUser.setLastName(faker.name().lastName());
            registrationService.registerUser(randomUser);
        }
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

        for (int i = 0; i < 500; i++) {
            OfferCreationInputModel offerCreationInputModel = createRandomOfferModel(brandNames);
            offerCreationInputModel.setPhoto("brand_logo.png");
            offerService.seedOffer(offerCreationInputModel, "d");
        }
        System.out.println("Офферы юзера 1 добавлены");
        for (int i = 0; i < 500; i++) {
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

}