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
import org.hibernate.search.engine.search.predicate.dsl.BooleanPredicateClausesStep;
import org.hibernate.search.engine.search.query.SearchResult;
import org.hibernate.search.engine.search.sort.SearchSort;
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
            int size,
            String sortBy,
            String searchTerm
    ) {
        Sort springSort = buildSpringSort(sortBy);
        Pageable pageable = PageRequest.of(page, size, springSort);

        if (searchTerm != null && !searchTerm.isBlank()) {
            // Полнотекстовый поиск через Hibernate Search
            SearchSession search = Search.session(entityManager);
            SearchResult<Offer> result = search.search(Offer.class)
                    .where(f -> {
                        var b = f.bool();
                        b.must(f.match()
                                .fields("gameName", "brand.name")
                                .matching(searchTerm));
                        if (priceFrom != null) b.must(f.range().field("price").atLeast(priceFrom));
                        if (priceTo != null) b.must(f.range().field("price").atMost(priceTo));
                        if (brand != null && !brand.isBlank()) {
                            b.must(f.match().field("brand.name").matching(brand));
                        }
                        if (Boolean.TRUE.equals(myOffers)) {
                            String user = SecurityContextHolder.getContext().getAuthentication().getName();
                            b.must(f.match().field("owner.username").matching(user));
                        }
                        return b;
                    })
                    .sort(f -> applySearchSort(f, sortBy))
                    .fetch((int) pageable.getOffset(), pageable.getPageSize());

            // Inline-маппинг сущностей в ViewModel
            List<OfferDemoViewModel> vms = result.hits().stream()
                    .map(offer -> {
                        OfferDemoViewModel vm = modelMapper.map(offer, OfferDemoViewModel.class);
                        vm.setOwner(offer.getOwner().getUsername());
                        return vm;
                    })
                    .toList();

            return new PageImpl<>(vms, pageable, result.total().hitCount());
        } else {
            // Обычная фильтрация через репозиторий JPA
            Page<Offer> pageOfOffers = offerRepository.findFilteredOffers(
                    priceFrom,
                    priceTo,
                    brand,
                    Boolean.TRUE.equals(myOffers),
                    SecurityContextHolder.getContext().getAuthentication().getName(),
                    pageable
            );
            // Inline-маппинг через Page.map
            return pageOfOffers.map(offer -> {
                OfferDemoViewModel vm = modelMapper.map(offer, OfferDemoViewModel.class);
                vm.setOwner(offer.getOwner().getUsername());
                return vm;
            });
        }
    }

    /**
     * 1) Spring Data Sort для Pageable
     */
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


    /**
     * 2) Hibernate Search Sort для полнотекста
     */
    private SortFinalStep applySearchSort(SearchSortFactory f, String sortBy) {
        return switch (sortBy) {
            case "priceAsc"  -> f.field("price").asc();
            case "priceDesc" -> f.field("price").desc();
            case "daysAsc"   -> f.field("maxRentalDays").asc();
            case "daysDesc"  -> f.field("maxRentalDays").desc();
            default          -> f.score();
        };
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