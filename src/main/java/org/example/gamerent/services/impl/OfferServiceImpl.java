package org.example.gamerent.services.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.example.gamerent.models.Brand;
import org.example.gamerent.models.Offer;
import org.example.gamerent.models.User;
import org.example.gamerent.models.consts.OfferDifficulty;
import org.example.gamerent.models.consts.OfferGenre;
import org.example.gamerent.models.consts.OfferStatus;
import org.example.gamerent.repos.BrandRepository;
import org.example.gamerent.repos.OfferRepository;
import org.example.gamerent.repos.RentalRepository;
import org.example.gamerent.repos.UserRepository;
import org.example.gamerent.services.OfferService;
import org.example.gamerent.web.viewmodels.OfferDemoViewModel;
import org.example.gamerent.web.viewmodels.OfferViewModel;
import org.example.gamerent.web.viewmodels.user_input.OfferCreationInputModel;
import org.example.gamerent.web.viewmodels.user_input.OfferUpdateInputModel;
import org.hibernate.search.engine.search.query.SearchResult;
import org.hibernate.search.engine.search.sort.dsl.SearchSortFactory;
import org.hibernate.search.engine.search.sort.dsl.SortFinalStep;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
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
import java.util.Random;


@Service
public class OfferServiceImpl implements OfferService {

    @Value("${offer.image.path}")
    private String photoUploadPath;


    private final OfferRepository offerRepository;
    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final BrandRepository brandRepository;


    @PersistenceContext
    private EntityManager entityManager;


    @Autowired
    public OfferServiceImpl(OfferRepository offerRepository, RentalRepository rentalRepository, UserRepository userRepository, ModelMapper modelMapper, BrandRepository brandRepository) {
        this.offerRepository = offerRepository;
        this.rentalRepository = rentalRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.brandRepository = brandRepository;
    }

    // Для инициализации
    @Override
    public void seedOffer(OfferCreationInputModel newOfferInputModel, String offerOwnerUsername) {
        Offer offerModel = modelMapper.map(newOfferInputModel, Offer.class);
        offerModel.setId(null);
        Brand brandModel = brandRepository.findBrandByName(newOfferInputModel.getBrand()).orElseThrow(() -> new RuntimeException("Бренд не найден"));
        offerModel.setBrand(brandModel);
        Random random = new Random();
        int randomNum = random.nextInt(1, 9);
        String randomPhoto = "image_" + randomNum + ".png";
        offerModel.setPhoto(randomPhoto);
        offerModel.setMinRentalDays(newOfferInputModel.getMinRentalDays());
        offerModel.setMaxRentalDays(newOfferInputModel.getMaxRentalDays());
        offerModel.setStatus(OfferStatus.AVAILABLE);
        User offerOwnerModel = userRepository.findUserByUsername(offerOwnerUsername).orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        offerModel.setOwner(offerOwnerModel);
        offerModel.setGenre(newOfferInputModel.getGenre());
        offerModel.setDifficulty(newOfferInputModel.getDifficulty());
        offerRepository.save(offerModel);
    }


    @Override
    @Transactional
    public Long createOffer(OfferCreationInputModel newOfferInputModel) {
        Offer offerModel = modelMapper.map(newOfferInputModel, Offer.class);
        offerModel.setId(null);
        offerModel.setMinRentalDays(newOfferInputModel.getMinRentalDays());
        offerModel.setMaxRentalDays(newOfferInputModel.getMaxRentalDays());
        Brand brandModel = brandRepository.findBrandByName(newOfferInputModel.getBrand()).orElseThrow(() -> new RuntimeException("Бренд не найден"));
        offerModel.setBrand(brandModel);
        MultipartFile offerPhoto = newOfferInputModel.getOfferPhoto();
        if (!offerPhoto.isEmpty()) {
            try {
                String fileName = System.currentTimeMillis() + "_" + offerPhoto.getOriginalFilename();
                Path filePath = Paths.get(photoUploadPath, fileName);
                Files.createDirectories(filePath.getParent());
                Files.copy(offerPhoto.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                offerModel.setPhoto(fileName);
            } catch (IOException e) {
                throw new RuntimeException("Ошибка загрузки файла");
            }
        } else {
            throw new RuntimeException("Оффер не имеет фото");
        }
        offerModel.setStatus(OfferStatus.AVAILABLE);
        String currentUserUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User offerOwnerModel = userRepository.findUserByUsername(currentUserUsername).orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        offerModel.setOwner(offerOwnerModel);
        offerModel.setGenre(newOfferInputModel.getGenre());
        offerModel.setDifficulty(newOfferInputModel.getDifficulty());
        offerModel = offerRepository.save(offerModel);
        return offerModel.getId();
    }

    @Override
    public Page<OfferDemoViewModel> getAllOffersFiltered(BigDecimal priceFrom, BigDecimal priceTo, String brandName, Boolean myOffers, int pageNumber, int pageSize, String sortBy, String searchTerm, List<OfferGenre> genres, List<OfferDifficulty> difficulties) {
        List<OfferGenre> normGenres = (genres != null && genres.isEmpty()) ? null : genres;
        List<OfferDifficulty> normDifficulties = (difficulties != null && difficulties.isEmpty()) ? null : difficulties;
        Sort springSort = buildSpringSort(sortBy);
        Pageable pageSettings = PageRequest.of(pageNumber, pageSize, springSort);
        if (searchTerm != null && !searchTerm.isBlank()) {
            SearchSession searchSession = Search.session(entityManager);
            SearchResult<Offer> searchResult = searchSession.search(Offer.class).where(f -> {
                var b = f.bool();
                b.must(f.match().fields("gameName", "brand.name").matching(searchTerm));
                if (priceFrom != null) b.must(f.range().field("price").atLeast(priceFrom));
                if (priceTo != null) b.must(f.range().field("price").atMost(priceTo));
                if (brandName != null && !brandName.isBlank()) {
                    b.must(f.match().field("brand.name").matching(brandName));
                }
                if (Boolean.TRUE.equals(myOffers)) {
                    String currentUserUsername = SecurityContextHolder.getContext().getAuthentication().getName();
                    b.must(f.match().field("owner.username").matching(currentUserUsername));
                }
                if (normGenres != null) {
                    b.must(f.bool(builder -> {
                        for (OfferGenre g : normGenres) {
                            builder.should(f.match().field("genre").matching(g));
                        }
                    }));
                }
                if (normDifficulties != null) {
                    b.must(f.bool(builder -> {
                        for (OfferDifficulty d : normDifficulties) {
                            builder.should(f.match().field("difficulty").matching(d));
                        }
                    }));
                }
                return b;
            }).sort(f -> buildSearchSort(f, sortBy)).fetch((int) pageSettings.getOffset(), pageSettings.getPageSize());
            List<OfferDemoViewModel> offerDemoViewModels = searchResult.hits().stream().map(offer -> {
                OfferDemoViewModel offerDemoViewModel = modelMapper.map(offer, OfferDemoViewModel.class);
                offerDemoViewModel.setOwner(offer.getOwner().getUsername());
                return offerDemoViewModel;
            }).toList();
            return new PageImpl<>(offerDemoViewModels, pageSettings, searchResult.total().hitCount());
        } else {
            Page<Offer> offersPage = offerRepository.findFilteredOffers(priceFrom, priceTo, brandName, Boolean.TRUE.equals(myOffers), SecurityContextHolder.getContext().getAuthentication().getName(), pageSettings, normGenres, normDifficulties);
            return offersPage.map(offer -> {
                OfferDemoViewModel offerDemoViewModel = modelMapper.map(offer, OfferDemoViewModel.class);
                offerDemoViewModel.setOwner(offer.getOwner().getUsername());
                return offerDemoViewModel;
            });
        }
    }


    @Override
    public OfferUpdateInputModel getOfferUpdateInputModel(Long offerId) {
        Offer offerModel = offerRepository.findById(offerId).orElseThrow(() -> new RuntimeException("Оффер не найден"));
        String currentUserUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!offerModel.getOwner().getUsername().equals(currentUserUsername)) {
            throw new RuntimeException("Доступ запрещен");
        }
        if (offerModel.getStatus() != OfferStatus.AVAILABLE) {
            throw new RuntimeException("Оффер недоступен. Изменения невозможны");
        }
        return modelMapper.map(offerModel, OfferUpdateInputModel.class);
    }

    @Override
    @Transactional
    public void updateOffer(Long offerId, OfferUpdateInputModel offerUpdateInputModel) {
        Offer offerModel = offerRepository.findById(offerId).orElseThrow(() -> new RuntimeException("Оффер не найден"));
        String currentUserUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!offerModel.getOwner().getUsername().equals(currentUserUsername)) {
            throw new RuntimeException("Доступ запрещен");
        }
        if (offerModel.getStatus() != OfferStatus.AVAILABLE) {
            throw new RuntimeException("Оффер недоступен. Изменения невозможны");
        }
        offerModel.setDescription(offerUpdateInputModel.getDescription());
        offerModel.setPrice(offerUpdateInputModel.getPrice());
        offerModel.setMinRentalDays(offerUpdateInputModel.getMinRentalDays());
        offerModel.setMaxRentalDays(offerUpdateInputModel.getMaxRentalDays());
        offerRepository.save(offerModel);
        Search.session(entityManager).indexingPlan().addOrUpdate(offerModel);
    }

    @Override
    @Transactional
    public void deleteOfferById(Long offerId) {
        Offer offerModel = offerRepository.findById(offerId).orElseThrow(() -> new RuntimeException("Оффер не найден"));
        String currentUserUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!offerModel.getOwner().getUsername().equals(currentUserUsername)) {
            throw new RuntimeException("Доступ запрещен");
        }
        if (offerModel.getStatus() != OfferStatus.AVAILABLE) {
            throw new RuntimeException("Оффер недоступен. Изменения невозможны");
        }
        offerRepository.delete(offerModel);
        Search.session(entityManager).indexingPlan().delete(offerModel);
    }

    @Override
    public OfferViewModel getOfferById(Long offerId) {
        Offer offerModel = offerRepository.findById(offerId).orElseThrow(() -> new RuntimeException("Оффер не найден"));
        OfferViewModel offerViewModel = modelMapper.map(offerModel, OfferViewModel.class);
        offerViewModel.setOwnerUsername(offerModel.getOwner().getUsername());
        return offerViewModel;
    }


    private Sort buildSpringSort(String sortBy) {
        if (sortBy == null || sortBy.isBlank()) {
            return Sort.unsorted();
        }
        return switch (sortBy) {
            case "priceAsc" -> Sort.by("price").ascending();
            case "priceDesc" -> Sort.by("price").descending();
            case "daysAsc" -> Sort.by("maxRentalDays").ascending();
            case "daysDesc" -> Sort.by("maxRentalDays").descending();
            default -> Sort.unsorted();
        };
    }

    private SortFinalStep buildSearchSort(SearchSortFactory searchSortFactory, String sortBy) {
        return switch (sortBy) {
            case "priceAsc" -> searchSortFactory.field("price").asc();
            case "priceDesc" -> searchSortFactory.field("price").desc();
            case "daysAsc" -> searchSortFactory.field("maxRentalDays").asc();
            case "daysDesc" -> searchSortFactory.field("maxRentalDays").desc();
            default -> searchSortFactory.score();
        };
    }

}