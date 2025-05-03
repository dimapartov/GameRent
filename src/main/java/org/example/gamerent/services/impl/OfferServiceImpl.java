package org.example.gamerent.services.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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


@Service
public class OfferServiceImpl implements OfferService {

    @Value("${offer.image.path}")
    private String uploadPath;


    private final OfferRepository offerRepository;
    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final BrandRepository brandRepository;


    @PersistenceContext
    private EntityManager entityManager;


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
    public void seedOffer(OfferCreationInputModel newOfferInputModel, String offerOwnerUsername) {
        Offer offer = modelMapper.map(newOfferInputModel, Offer.class);
        offer.setId(null);
        Brand brand = brandRepository.findBrandByName(newOfferInputModel.getBrand()).orElseThrow(() -> new RuntimeException("Бренд не найден"));
        offer.setBrand(brand);
        offer.setPhoto(newOfferInputModel.getPhoto());
        offer.setMinRentalDays(newOfferInputModel.getMinRentalDays());
        offer.setMaxRentalDays(newOfferInputModel.getMaxRentalDays());
        offer.setStatus(OfferStatus.AVAILABLE);
        User owner = userRepository.findUserByUsername(offerOwnerUsername).orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        offer.setOwner(owner);
        offerRepository.save(offer);
    }


    @Override
    public Long createOffer(OfferCreationInputModel newOfferInputModel, MultipartFile offerPhoto) {
        Offer offer = modelMapper.map(newOfferInputModel, Offer.class);
        offer.setId(null);
        offer.setMinRentalDays(newOfferInputModel.getMinRentalDays());
        offer.setMaxRentalDays(newOfferInputModel.getMaxRentalDays());
        Brand brand = brandRepository.findBrandByName(newOfferInputModel.getBrand()).orElseThrow(() -> new RuntimeException("Бренд не найден"));
        offer.setBrand(brand);
        if (!offerPhoto.isEmpty()) {
            try {
                String fileName = System.currentTimeMillis() + "_" + offerPhoto.getOriginalFilename();
                Path filePath = Paths.get(uploadPath, fileName);
                Files.createDirectories(filePath.getParent());
                Files.copy(offerPhoto.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                offer.setPhoto(fileName);
            } catch (IOException e) {
                throw new RuntimeException("Ошибка загрузки файла", e);
            }
        } else {
            throw new RuntimeException("Оффер не имеет фото");
        }
        offer.setStatus(OfferStatus.AVAILABLE);
        String currentUserUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User owner = userRepository.findUserByUsername(currentUserUsername).orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        offer.setOwner(owner);
        offer = offerRepository.save(offer);
        return offer.getId();
    }

    @Override
    public Page<OfferDemoViewModel> getAllOffersFiltered(
            BigDecimal priceFrom,
            BigDecimal priceTo,
            String brand,
            Boolean myOffers,
            int page,
            int size,
            String sortBy,
            String searchTerm
    ) {
        Sort springSort = buildSpringSort(sortBy);
        Pageable pageable = PageRequest.of(page, size, springSort);

        if (searchTerm != null && !searchTerm.isBlank()) {
            SearchSession searchSession = Search.session(entityManager);
            SearchResult<Offer> searchResult = searchSession.search(Offer.class)
                    .where(f -> {
                        var b = f.bool();
                        b.must(f.match().fields("gameName", "brand.name").matching(searchTerm));
                        if (priceFrom != null) b.must(f.range().field("price").atLeast(priceFrom));
                        if (priceTo != null) b.must(f.range().field("price").atMost(priceTo));
                        if (brand != null && !brand.isBlank()) {
                            b.must(f.match().field("brand.name").matching(brand));
                        }
                        if (Boolean.TRUE.equals(myOffers)) {
                            String currentUserUsername = SecurityContextHolder.getContext().getAuthentication().getName();
                            b.must(f.match().field("owner.username").matching(currentUserUsername));
                        }
                        return b;
                    })
                    .sort(f -> buildSearchSort(f, sortBy))
                    .fetch((int) pageable.getOffset(), pageable.getPageSize());
            List<OfferDemoViewModel> offerViewModels = searchResult.hits().stream()
                    .map(offer -> {
                        OfferDemoViewModel offerDemoViewModel = modelMapper.map(offer, OfferDemoViewModel.class);
                        offerDemoViewModel.setOwner(offer.getOwner().getUsername());
                        return offerDemoViewModel;
                    })
                    .toList();
            return new PageImpl<>(offerViewModels, pageable, searchResult.total().hitCount());
        } else {
            Page<Offer> filteredOffers = offerRepository.findFilteredOffers(
                    priceFrom,
                    priceTo,
                    brand,
                    Boolean.TRUE.equals(myOffers),
                    SecurityContextHolder.getContext().getAuthentication().getName(),
                    pageable
            );
            return filteredOffers.map(offer -> {
                OfferDemoViewModel offerDemoViewModel = modelMapper.map(offer, OfferDemoViewModel.class);
                offerDemoViewModel.setOwner(offer.getOwner().getUsername());
                return offerDemoViewModel;
            });
        }
    }

    @Override
    public OfferUpdateInputModel getOfferUpdateModel(Long id) {
        Offer offer = offerRepository.findById(id).orElseThrow(() -> new RuntimeException("Оффер не найден"));
        String currentUserUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!offer.getOwner().getUsername().equals(currentUserUsername)) {
            throw new RuntimeException("Доступ запрещен");
        }
        if (offer.getStatus() != OfferStatus.AVAILABLE) {
            throw new RuntimeException("Оффер недоступен. Изменения невозможны");
        }
        return modelMapper.map(offer, OfferUpdateInputModel.class);
    }

    @Override
    public void updateOffer(Long id, OfferUpdateInputModel offerUpdateInputModel) {
        Offer offer = offerRepository.findById(id).orElseThrow(() -> new RuntimeException("Оффер не найден"));
        String currentUserUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!offer.getOwner().getUsername().equals(currentUserUsername)) {
            throw new RuntimeException("Доступ запрещен");
        }
        if (offer.getStatus() != OfferStatus.AVAILABLE) {
            throw new RuntimeException("Оффер недоступен. Изменения невозможны");
        }
        offer.setDescription(offerUpdateInputModel.getDescription());
        offer.setPrice(offerUpdateInputModel.getPrice());
        offer.setMinRentalDays(offerUpdateInputModel.getMinRentalDays());
        offer.setMaxRentalDays(offerUpdateInputModel.getMaxRentalDays());
        offerRepository.save(offer);
        Search.session(entityManager).indexingPlan().addOrUpdate(offer);
    }

    @Override
    public void deleteOfferById(Long id) {
        Offer offer = offerRepository.findById(id).orElseThrow(() -> new RuntimeException("Оффер не найден"));
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!offer.getOwner().getUsername().equals(currentUser)) {
            throw new RuntimeException("Доступ запрещен");
        }
        if (offer.getStatus() != OfferStatus.AVAILABLE) {
            throw new RuntimeException("Оффер недоступен. Изменения невозможны");
        }
        offerRepository.delete(offer);
        Search.session(entityManager).indexingPlan().delete(offer);
    }

    @Override
    public OfferViewModel getOfferById(Long id) {
        Offer offer = offerRepository.findById(id).orElseThrow(() -> new RuntimeException("Оффер не найден"));
        OfferViewModel offerViewModel = modelMapper.map(offer, OfferViewModel.class);
        offerViewModel.setOwnerUsername(offer.getOwner().getUsername());
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