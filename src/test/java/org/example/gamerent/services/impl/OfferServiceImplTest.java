package org.example.gamerent.services.impl;

import jakarta.persistence.EntityManager;
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
import org.example.gamerent.web.viewmodels.OfferDemoViewModel;
import org.example.gamerent.web.viewmodels.OfferViewModel;
import org.example.gamerent.web.viewmodels.user_input.OfferCreationInputModel;
import org.example.gamerent.web.viewmodels.user_input.OfferUpdateInputModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class OfferServiceImplTest {

    private static final String USER = "ownerUser";
    @Mock
    private OfferRepository offerRepository;
    @Mock
    private BrandRepository brandRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RentalRepository rentalRepository;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private MultipartFile multipartFile;
    @Mock
    private EntityManager entityManager;
    @InjectMocks
    private OfferServiceImpl offerService;
    private User owner;
    private Brand brand;

    @BeforeEach
    void setUp() {
        // Inject private fields
        ReflectionTestUtils.setField(offerService, "entityManager", entityManager);
        ReflectionTestUtils.setField(offerService, "photoUploadPath", System.getProperty("java.io.tmpdir"));

        // Security context
        owner = new User();
        owner.setUsername(USER);
        SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken(USER, null));
        brand = new Brand();
    }

    @Test
    void seedOffer_success() {
        OfferCreationInputModel input = new OfferCreationInputModel();
        input.setBrand("brandName");
        input.setMinRentalDays(2);
        input.setMaxRentalDays(4);
        input.setGenre(OfferGenre.CARDS);
        input.setDifficulty(OfferDifficulty.EASY);
        when(modelMapper.map(input, Offer.class)).thenReturn(new Offer());
        when(brandRepository.findBrandByName("brandName")).thenReturn(Optional.of(brand));
        when(userRepository.findUserByUsername(USER)).thenReturn(Optional.of(owner));

        offerService.seedOffer(input, USER);

        verify(offerRepository).save(argThat(o ->
                o.getBrand() == brand && o.getOwner() == owner && o.getStatus() == OfferStatus.AVAILABLE
        ));
    }

    @Test
    void seedOffer_brandNotFound() {
        OfferCreationInputModel input = new OfferCreationInputModel();
        input.setBrand("brandName");
        // stub mapping to avoid null offerModel
        when(modelMapper.map(input, Offer.class)).thenReturn(new Offer());
        when(brandRepository.findBrandByName("brandName")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> offerService.seedOffer(input, USER));
        assertEquals("Бренд не найден", ex.getMessage());
    }

    @Test
    void seedOffer_userNotFound() {
        OfferCreationInputModel input = new OfferCreationInputModel();
        input.setBrand("brandName");
        // stub mapping to avoid null offerModel
        when(modelMapper.map(input, Offer.class)).thenReturn(new Offer());
        when(brandRepository.findBrandByName("brandName")).thenReturn(Optional.of(brand));
        when(userRepository.findUserByUsername(USER)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> offerService.seedOffer(input, USER));
        assertEquals("Пользователь не найден", ex.getMessage());
    }

    @Test
    void createOffer_success() throws IOException {
        OfferCreationInputModel input = new OfferCreationInputModel();
        input.setBrand("brandName");
        input.setMinRentalDays(1);
        input.setMaxRentalDays(5);
        input.setGenre(OfferGenre.CLASSIC);
        input.setDifficulty(OfferDifficulty.HARD);
        input.setOfferPhoto(multipartFile);

        Offer entity = new Offer();
        when(modelMapper.map(input, Offer.class)).thenReturn(entity);
        when(brandRepository.findBrandByName("brandName")).thenReturn(Optional.of(brand));
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getOriginalFilename()).thenReturn("game.png");
        when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[]{1, 2, 3}));
        when(userRepository.findUserByUsername(USER)).thenReturn(Optional.of(owner));
        when(offerRepository.save(entity)).thenAnswer(inv -> {
            entity.setId(1L);
            return entity;
        });

        Long id = offerService.createOffer(input);
        assertEquals(1L, id);
        verify(offerRepository).save(entity);
    }

    @Test
    void createOffer_brandNotFound_throws() {
        OfferCreationInputModel input = new OfferCreationInputModel();
        input.setBrand("brandName");
        input.setOfferPhoto(multipartFile);
        when(modelMapper.map(input, Offer.class)).thenReturn(new Offer());
        when(brandRepository.findBrandByName("brandName")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> offerService.createOffer(input));
        assertEquals("Бренд не найден", ex.getMessage());
    }

    @Test
    void createOffer_noPhoto_throws() {
        OfferCreationInputModel input = new OfferCreationInputModel();
        input.setBrand("brandName");
        input.setOfferPhoto(multipartFile);
        when(modelMapper.map(input, Offer.class)).thenReturn(new Offer());
        when(brandRepository.findBrandByName("brandName")).thenReturn(Optional.of(brand));
        when(multipartFile.isEmpty()).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> offerService.createOffer(input));
        assertEquals("Оффер не имеет фото", ex.getMessage());
    }

    @Test
    void getAllOffersFiltered_noSearch_returnsDemoModels() {
        Offer o = new Offer();
        o.setOwner(owner);
        var pageRequest = PageRequest.of(0, 1);
        Page<Offer> page = new PageImpl<>(List.of(o), pageRequest, 1);
        when(offerRepository.findFilteredOffers(any(), any(), any(), anyBoolean(), anyString(), eq(pageRequest), any(), any()))
                .thenReturn(page);
        when(modelMapper.map(o, OfferDemoViewModel.class)).thenAnswer(inv -> {
            OfferDemoViewModel vm = new OfferDemoViewModel();
            vm.setOwner(owner.getUsername());
            return vm;
        });

        var result = offerService.getAllOffersFiltered(null, null, null, false, 0, 1, null, null, List.of(), List.of());
        assertEquals(1, result.getContent().size());
        assertEquals(USER, result.getContent().get(0).getOwner());
    }

    @Test
    void getOfferById_success() {
        Offer o = new Offer();
        o.setOwner(owner);
        OfferViewModel vm = new OfferViewModel();
        when(offerRepository.findById(5L)).thenReturn(Optional.of(o));
        when(modelMapper.map(o, OfferViewModel.class)).thenReturn(vm);

        OfferViewModel res = offerService.getOfferById(5L);
        assertSame(vm, res);
        assertEquals(owner.getUsername(), res.getOwnerUsername());
    }

    @Test
    void getOfferUpdateInputModel_validatesAccess() {
        Offer o = new Offer();
        o.setOwner(owner);
        o.setStatus(OfferStatus.AVAILABLE);
        when(offerRepository.findById(6L)).thenReturn(Optional.of(o));
        OfferUpdateInputModel update = new OfferUpdateInputModel();
        when(modelMapper.map(o, OfferUpdateInputModel.class)).thenReturn(update);

        var res = offerService.getOfferUpdateInputModel(6L);
        assertSame(update, res);
    }

    @Test
    void updateOffer_notAllowed_throws() {
        Offer o = new Offer();
        o.setOwner(new User());
        o.getOwner().setUsername("other");
        o.setStatus(OfferStatus.AVAILABLE);
        when(offerRepository.findById(anyLong())).thenReturn(Optional.of(o));
        RuntimeException ex = assertThrows(RuntimeException.class, () -> offerService.updateOffer(9L, new OfferUpdateInputModel()));
        assertEquals("Доступ запрещен", ex.getMessage());
    }

    @Test
    void deleteOffer_notAllowed_throws() {
        Offer o = new Offer();
        o.setOwner(owner);
        o.setStatus(OfferStatus.RENTED);
        when(offerRepository.findById(anyLong())).thenReturn(Optional.of(o));
        RuntimeException ex = assertThrows(RuntimeException.class, () -> offerService.deleteOfferById(10L));
        assertEquals("Оффер недоступен. Изменения невозможны", ex.getMessage());
    }

}