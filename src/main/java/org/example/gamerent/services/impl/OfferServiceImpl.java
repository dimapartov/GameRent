package org.example.gamerent.services.impl;

import org.example.gamerent.models.Brand;
import org.example.gamerent.models.Offer;
import org.example.gamerent.models.User;
import org.example.gamerent.models.consts.OfferStatus;
import org.example.gamerent.repos.BrandRepository;
import org.example.gamerent.repos.OfferRepository;
import org.example.gamerent.repos.RentalRepository;
import org.example.gamerent.repos.UserRepository;
import org.example.gamerent.services.OfferService;
import org.example.gamerent.web.viewmodels.OfferDemoViewModel;
import org.example.gamerent.web.viewmodels.user_input.OfferCreationInputModel;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
public class OfferServiceImpl implements OfferService {

    private final OfferRepository offerRepository;
    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final BrandRepository brandRepository;


    @Autowired
    public OfferServiceImpl(OfferRepository offerRepository, RentalRepository rentalRepository, UserRepository userRepository, ModelMapper modelMapper, BrandRepository brandRepository) {
        this.offerRepository = offerRepository;
        this.rentalRepository = rentalRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.brandRepository = brandRepository;
    }


    @Value("${upload.path}")
    private String uploadPath;


    @Override
    public OfferCreationInputModel createOffer(OfferCreationInputModel newOffer, MultipartFile photo) {
        Offer offer = modelMapper.map(newOffer, Offer.class);
        offer.setId(null);
        Brand brand = brandRepository.findByName(newOffer.getBrand());
        offer.setBrand(brand);
        if (!photo.isEmpty()) {
            try {
                String filename = System.currentTimeMillis() + "_" + photo.getOriginalFilename();
                Path filePath = Paths.get(uploadPath, filename);
                Files.createDirectories(filePath.getParent());
                Files.copy(photo.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                offer.setPhoto(filename);
            } catch (IOException e) {
                throw new RuntimeException("Ошибка загрузки файла", e);
            }
        }
        offer.setStatus(OfferStatus.AVAILABLE);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        User owner = userRepository.findUserByUsername(currentPrincipalName).orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        offer.setOwner(owner);
        offer = offerRepository.save(offer);
        return modelMapper.map(offer, OfferCreationInputModel.class);
    }

    @Override
    public List<OfferDemoViewModel> getAllOffersFiltered(BigDecimal priceFrom, BigDecimal priceTo, String brand, Boolean myOffers) {
        // Получаем все офферы
        List<Offer> offers = offerRepository.findAll();
        Stream<Offer> stream = offers.stream();

        // Фильтрация по цене от
        if (priceFrom != null) {
            stream = stream.filter(o -> o.getPrice().compareTo(priceFrom) >= 0);
        }

        // Фильтрация по цене до
        if (priceTo != null) {
            stream = stream.filter(o -> o.getPrice().compareTo(priceTo) <= 0);
        }

        // Фильтрация по бренду (если передана строка, сравнение без учета регистра)
        if (brand != null && !brand.trim().isEmpty()) {
            stream = stream.filter(o -> o.getBrand().getName().equalsIgnoreCase(brand.trim()));
        }

        // Если включен режим "Мои объявления", то получить имя залогиненного пользователя
        if (myOffers != null && myOffers) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentPrincipalName = authentication.getName();
            stream = stream.filter(o -> o.getOwner().getUsername().equals(currentPrincipalName));
        }

        // Маппинг Offer в OfferDemoViewModel
        return stream.map(offer -> {
            OfferDemoViewModel offerDemoViewModel = modelMapper.map(offer, OfferDemoViewModel.class);
            offerDemoViewModel.setOwner(offer.getOwner().getUsername());
            return offerDemoViewModel;
        }).collect(Collectors.toList());
    }

}