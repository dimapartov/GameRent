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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class OfferServiceImpl implements OfferService {

    private final OfferRepository offerRepository;
    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final BrandRepository brandRepository;

    @Autowired
    public OfferServiceImpl(OfferRepository offerRepository,
                            RentalRepository rentalRepository,
                            UserRepository userRepository,
                            ModelMapper modelMapper, BrandRepository brandRepository) {
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
    public List<OfferDemoViewModel> getAllOffersDemoViewModels() {
        return offerRepository.findAll()
                .stream()
                .map(offer -> {
                    OfferDemoViewModel offerDemoViewModel = modelMapper.map(offer, OfferDemoViewModel.class);
                    offerDemoViewModel.setOwner(offer.getOwner().getUsername());
                    return offerDemoViewModel;
                })
                .collect(Collectors.toList());
    }


}