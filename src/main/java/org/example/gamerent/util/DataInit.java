package org.example.gamerent.util;

import com.github.javafaker.Faker;
import org.example.gamerent.services.*;
import org.example.gamerent.services.impl.security.RegistrationService;
import org.example.gamerent.web.viewmodels.BrandViewModel;
import org.example.gamerent.web.viewmodels.user_input.BrandCreationInputModel;
import org.example.gamerent.web.viewmodels.user_input.GameCreationInputModel;
import org.example.gamerent.web.viewmodels.user_input.RegistrationInputModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;


@Component
public class DataInit implements CommandLineRunner {

    private final BrandService brandService;
    private final GameService gameService;
    private final UserService userService;
    private final OfferService offerService;
    private final RentalService rentalService;
    private final RegistrationService registrationService;

    @Autowired
    public DataInit(BrandService brandService, GameService gameService, UserService userService, OfferService offerService, RentalService rentalService, RegistrationService registrationService) {
        this.brandService = brandService;
        this.gameService = gameService;
        this.userService = userService;
        this.offerService = offerService;
        this.rentalService = rentalService;
        this.registrationService = registrationService;
    }

    @Override
    public void run(String... args) throws Exception {
        seedData();
    }

    private void seedData() {
        Faker faker = new Faker();
        Random random = new Random();


        RegistrationInputModel testUserModel = new RegistrationInputModel();
        testUserModel.setUsername("dima");
        testUserModel.setEmail("dima@dima.ru");
        testUserModel.setPassword("dima");
        testUserModel.setFirstName("Dima");
        testUserModel.setLastName("Partov");
        registrationService.registerUser(testUserModel);
        for (int i = 0; i < 5; i++) {
            RegistrationInputModel registrationInputModel = new RegistrationInputModel();
            registrationInputModel.setUsername(faker.name().username());
            registrationInputModel.setEmail(faker.internet().emailAddress());
            registrationInputModel.setPassword(faker.internet().password());
            registrationInputModel.setFirstName(faker.name().firstName());
            registrationInputModel.setLastName(faker.name().lastName());
            registrationService.registerUser(registrationInputModel);
        }
        for (int i = 0; i < 5; i++) {
            BrandCreationInputModel brandCreationInputModel = new BrandCreationInputModel();
            brandCreationInputModel.setName(faker.company().name());
            brandCreationInputModel.setDescription(faker.lorem().sentence());
            brandCreationInputModel.setPhoto(faker.internet().image());
            brandService.createBrand(brandCreationInputModel);
        }
        List<BrandViewModel> allBrands = brandService.getAllBrands();
        int randomIndex = random.nextInt(allBrands.size());
        BrandViewModel randomBrand = allBrands.get(randomIndex);
        Long brandIdForGamesCreation = randomBrand.getId();
        for (int i = 0; i < 5; i++) {
            GameCreationInputModel gameCreationInputModel = new GameCreationInputModel();
            gameCreationInputModel.setName(faker.gameOfThrones().city());
            gameCreationInputModel.setDescription(faker.lorem().sentence());
            gameCreationInputModel.setBrandId(brandIdForGamesCreation);
            gameService.createGame(gameCreationInputModel);
        }

    }
}