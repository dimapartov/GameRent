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


    @Value("${upload.path}")
    private String uploadPath;


    @Autowired
    public OfferServiceImpl(OfferRepository offerRepository,
                            RentalRepository rentalRepository,
                            UserRepository userRepository,
                            ModelMapper modelMapper,
                            BrandRepository brandRepository) {
        this.offerRepository = offerRepository;
        this.rentalRepository = rentalRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.brandRepository = brandRepository;
    }


    @Override
    public OfferCreationInputModel createOffer(OfferCreationInputModel newOffer, MultipartFile photo) {
        Offer offer = modelMapper.map(newOffer, Offer.class);
        offer.setId(null);
        offer.setMinRentalDays(newOffer.getMinRentalDays());
        offer.setMaxRentalDays(newOffer.getMaxRentalDays());
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
        String currentPrincipalName = SecurityContextHolder.getContext().getAuthentication().getName();
        User owner = userRepository.findUserByUsername(currentPrincipalName)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        offer.setOwner(owner);
        offer = offerRepository.save(offer);
        return modelMapper.map(offer, OfferCreationInputModel.class);
    }

    @Override
    public List<OfferDemoViewModel> getAllOffersFiltered(BigDecimal priceFrom,
                                                         BigDecimal priceTo,
                                                         String brand,
                                                         Boolean myOffers) {
        List<Offer> offers = offerRepository.findAll();
        Stream<Offer> stream = offers.stream();
        if (priceFrom != null) {
            stream = stream.filter(o -> o.getPrice().compareTo(priceFrom) >= 0);
        }
        if (priceTo != null) {
            stream = stream.filter(o -> o.getPrice().compareTo(priceTo) <= 0);
        }
        if (brand != null && !brand.trim().isEmpty()) {
            stream = stream.filter(o -> o.getBrand().getName().equalsIgnoreCase(brand.trim()));
        }
        if (Boolean.TRUE.equals(myOffers)) {
            String currentPrincipalName = SecurityContextHolder.getContext().getAuthentication().getName();
            stream = stream.filter(o -> o.getOwner().getUsername().equals(currentPrincipalName));
        }
        return stream.map(o -> {
            OfferDemoViewModel vm = modelMapper.map(o, OfferDemoViewModel.class);
            vm.setOwner(o.getOwner().getUsername());
            return vm;
        }).collect(Collectors.toList());
    }

}