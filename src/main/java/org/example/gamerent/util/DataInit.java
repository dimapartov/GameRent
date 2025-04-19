package org.example.gamerent.util;

import com.github.javafaker.Faker;
import org.example.gamerent.services.BrandService;
import org.example.gamerent.services.OfferService;
import org.example.gamerent.services.impl.security.RegistrationService;
import org.example.gamerent.web.viewmodels.user_input.BrandCreationInputModel;
import org.example.gamerent.web.viewmodels.user_input.RegistrationInputModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Random;


@Component
public class DataInit implements CommandLineRunner {

    private final BrandService brandService;
    private final OfferService offerService;
    private final RegistrationService registrationService;


    @Autowired
    public DataInit(BrandService brandService, OfferService offerService, RegistrationService registrationService) {
        this.brandService = brandService;
        this.offerService = offerService;
        this.registrationService = registrationService;
    }


    @Override
    public void run(String... args) throws Exception {
        seedUsers();
        seedBrands();
    }


    Faker faker = new Faker();
    Random random = new Random();

    private void seedUsers() {
        RegistrationInputModel testUserModel = new RegistrationInputModel();
        testUserModel.setUsername("dima");
        testUserModel.setEmail("dima@dima.ru");
        testUserModel.setPassword("dima");
        testUserModel.setFirstName("Dima");
        testUserModel.setLastName("Partov");
        registrationService.registerUser(testUserModel);

        RegistrationInputModel testUserModel2 = new RegistrationInputModel();
        testUserModel2.setUsername("dima2");
        testUserModel2.setEmail("dima2@dima.ru");
        testUserModel2.setPassword("dima2");
        testUserModel2.setFirstName("Dima");
        testUserModel2.setLastName("Kubarev");
        registrationService.registerUser(testUserModel2);

        for (int i = 0; i < 4; i++) {
            RegistrationInputModel registrationInputModel = new RegistrationInputModel();
            registrationInputModel.setUsername(faker.name().username());
            registrationInputModel.setEmail(faker.internet().emailAddress());
            registrationInputModel.setPassword(faker.internet().password());
            registrationInputModel.setFirstName(faker.name().firstName());
            registrationInputModel.setLastName(faker.name().lastName());
            registrationService.registerUser(registrationInputModel);
        }
    }

    private void seedBrands() {
        for (int i = 0; i < 5; i++) {
            BrandCreationInputModel brandCreationInputModel = new BrandCreationInputModel();
            brandCreationInputModel.setName(faker.company().name());
            brandCreationInputModel.setDescription(faker.lorem().sentence());
            brandCreationInputModel.setPhoto(faker.internet().image());
            brandService.createBrand(brandCreationInputModel);
        }
    }

}