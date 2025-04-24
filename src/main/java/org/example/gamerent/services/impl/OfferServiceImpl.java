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
import org.example.gamerent.web.viewmodels.OfferViewModel;
import org.example.gamerent.web.viewmodels.user_input.OfferCreationInputModel;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;


@Service
public class OfferServiceImpl implements OfferService {

    private final OfferRepository offerRepository;
    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final BrandRepository brandRepository;


    @Value("${offer.image.path}")
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

//    Для инициализации данных
    @Override
    public void seedOffer(OfferCreationInputModel newOffer, String username) {
        Offer offer = modelMapper.map(newOffer, Offer.class);
        offer.setId(null);
        offer.setBrand(brandRepository.findByName(newOffer.getBrand()));
        offer.setPhoto(newOffer.getPhoto());
        offer.setMinRentalDays(newOffer.getMinRentalDays());
        offer.setMaxRentalDays(newOffer.getMaxRentalDays());
        offer.setStatus(OfferStatus.AVAILABLE);
        User owner = userRepository.findUserByUsername(username).orElseThrow(() -> new RuntimeException("Пользователь не найден: " + username));
        offer.setOwner(owner);
        offerRepository.save(offer);
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
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        User owner = userRepository.findUserByUsername(currentUser).orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        offer.setOwner(owner);
        offer = offerRepository.save(offer);
        return modelMapper.map(offer, OfferCreationInputModel.class);
    }

    @Override
    public Page<OfferDemoViewModel> getAllOffersFiltered(
            BigDecimal priceFrom,
            BigDecimal priceTo,
            String brand,
            Boolean myOffers,
            int page,
            int size
    ) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        Pageable pageable = PageRequest.of(page, size);
        Page<Offer> pageOfOffers = offerRepository.findFilteredOffers(
                priceFrom,
                priceTo,
                brand,
                Boolean.TRUE.equals(myOffers),
                currentUsername,
                pageable
        );
        return pageOfOffers.map(offer -> {
            OfferDemoViewModel offerDemoViewModel = modelMapper.map(offer, OfferDemoViewModel.class);
            offerDemoViewModel.setOwner(offer.getOwner().getUsername());
            return offerDemoViewModel;
        });
    }



    @Override
    public OfferViewModel getById(Long id) {
        Offer offer = offerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Offer not found"));
        OfferViewModel viewModel = modelMapper.map(offer, OfferViewModel.class);
        viewModel.setOwnerUsername(offer.getOwner().getUsername());
        return viewModel;
    }
}