package org.example.gamerent.services.impl;

import org.example.gamerent.models.Offer;
import org.example.gamerent.models.User;
import org.example.gamerent.models.consts.OfferStatus;
import org.example.gamerent.repos.OfferRepository;
import org.example.gamerent.repos.RentalRepository;
import org.example.gamerent.repos.UserRepository;
import org.example.gamerent.services.OfferService;
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

@Service
public class OfferServiceImpl implements OfferService {

    private final OfferRepository offerRepository;
    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public OfferServiceImpl(OfferRepository offerRepository,
                            RentalRepository rentalRepository,
                            UserRepository userRepository,
                            ModelMapper modelMapper) {
        this.offerRepository = offerRepository;
        this.rentalRepository = rentalRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Value("${upload.path}")
    private String uploadPath;

    @Override
    public OfferCreationInputModel createOffer(OfferCreationInputModel newOffer, MultipartFile image) {
        // Преобразование DTO в сущность Offer
        Offer offer = modelMapper.map(newOffer, Offer.class);
        // Гарантируем, что id = null (новая запись), чтобы избежать ошибки обновления
        offer.setId(null);

        // Обработка загрузки файла
        if (!image.isEmpty()) {
            try {
                String filename = System.currentTimeMillis() + "_" + image.getOriginalFilename();
                Path filePath = Paths.get(uploadPath, filename);
                Files.createDirectories(filePath.getParent());
                Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                offer.setPhoto(filename);
            } catch (IOException e) {
                throw new RuntimeException("Ошибка загрузки файла", e);
            }
        }
        // Если статус не задан, устанавливаем значение по умолчанию
        if (offer.getStatus() == null) {
            offer.setStatus(OfferStatus.AVAILABLE); // Предполагается, что такой константой есть в OfferStatus
        }
        // Установка владельца оффера по имени, полученному из Security (автентифицированного пользователя)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        User owner = userRepository.findUserByUsername(currentPrincipalName)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        offer.setOwner(owner);
        offer = offerRepository.save(offer);
        return modelMapper.map(offer, OfferCreationInputModel.class);
    }
}