package org.example.gamerent.services.impl;

import org.example.gamerent.models.Offer;
import org.example.gamerent.models.Rental;
import org.example.gamerent.models.User;
import org.example.gamerent.models.consts.OfferStatus;
import org.example.gamerent.models.consts.RentalStatus;
import org.example.gamerent.repos.OfferRepository;
import org.example.gamerent.repos.RentalRepository;
import org.example.gamerent.repos.UserRepository;
import org.example.gamerent.services.dto.RentalDTO;
import org.example.gamerent.web.viewmodels.RentalViewModel;
import org.example.gamerent.web.viewmodels.user_input.RentalRequestInputModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class RentalServiceImplTest {

    @Mock
    private RentalRepository rentalRepository;
    @Mock
    private OfferRepository offerRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private MailSenderService mailSenderService;

    @InjectMocks
    private RentalServiceImpl rentalService;

    private static final String RENTER = "renterUser";
    private User renter;
    private User owner;
    private Offer offer;

    @BeforeEach
    void setUp() {
        renter = new User();
        renter.setUsername(RENTER);
        renter.setEmail("renter@example.com");

        owner = new User();
        owner.setUsername("ownerUser");
        owner.setEmail("owner@example.com");

        offer = new Offer();
        offer.setId(10L);
        offer.setOwner(owner);
        offer.setGameName("Game");
        offer.setStatus(OfferStatus.AVAILABLE);

        SecurityContextHolder.getContext()
                .setAuthentication(new TestingAuthenticationToken(RENTER, null));
    }

    @Test
    void createRentalRequest_success() {
        RentalRequestInputModel input = new RentalRequestInputModel(10L, 3);

        when(offerRepository.findById(10L)).thenReturn(Optional.of(offer));
        when(userRepository.findUserByUsername(RENTER)).thenReturn(Optional.of(renter));
        when(modelMapper.map(any(RentalDTO.class), eq(Rental.class)))
                .thenAnswer(inv -> {
                    Rental r = new Rental();
                    r.setStatus(RentalStatus.PENDING_FOR_CONFIRM);
                    r.setOffer(offer);
                    r.setRenter(renter);
                    r.setDays(input.getDays());
                    return r;
                });

        rentalService.createRentalRequest(input);

        ArgumentCaptor<Rental> rentalCaptor = ArgumentCaptor.forClass(Rental.class);
        verify(rentalRepository).save(rentalCaptor.capture());
        Rental saved = rentalCaptor.getValue();
        assertEquals(RentalStatus.PENDING_FOR_CONFIRM, saved.getStatus());

        verify(offerRepository).save(offer);
        assertEquals(OfferStatus.BOOKED, offer.getStatus());

        verify(mailSenderService).sendSimpleMail(
                eq("owner@example.com"),
                contains("Новый запрос на аренду"),
                anyString()
        );
    }

    @Test
    void createRentalRequest_offerNotFound_throws() {
        when(offerRepository.findById(anyLong())).thenReturn(Optional.empty());
        RentalRequestInputModel input = new RentalRequestInputModel(99L, 2);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> rentalService.createRentalRequest(input)
        );
        assertEquals("Оффер не найден", ex.getMessage());
    }

    @Test
    void createRentalRequest_unavailableOffer_throws() {
        offer.setStatus(OfferStatus.RENTED);
        when(offerRepository.findById(10L)).thenReturn(Optional.of(offer));
        RentalRequestInputModel input = new RentalRequestInputModel(10L, 2);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> rentalService.createRentalRequest(input)
        );
        assertEquals("Оффер недоступен", ex.getMessage());
    }

    @Test
    void createRentalRequest_selfRent_throws() {
        offer.setOwner(renter);
        offer.setStatus(OfferStatus.AVAILABLE);
        when(offerRepository.findById(10L)).thenReturn(Optional.of(offer));
        when(userRepository.findUserByUsername(RENTER)).thenReturn(Optional.of(renter));
        RentalRequestInputModel input = new RentalRequestInputModel(10L, 2);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> rentalService.createRentalRequest(input)
        );
        assertEquals("Нельзя арендовать собственный оффер", ex.getMessage());
    }

    @Test
    void cancelRentalRequest_success() {
        Rental r = new Rental();
        r.setStatus(RentalStatus.PENDING_FOR_CONFIRM);
        r.setOffer(offer);
        when(rentalRepository.findById(1L)).thenReturn(Optional.of(r));

        rentalService.cancelRentalRequest(1L);

        assertEquals(RentalStatus.CANCELED_BY_RENTER, r.getStatus());
        verify(rentalRepository).save(r);
        verify(offerRepository).save(offer);
        assertEquals(OfferStatus.AVAILABLE, offer.getStatus());
    }

    @Test
    void cancelRentalRequest_invalidStatus_throws() {
        Rental r = new Rental();
        r.setStatus(RentalStatus.ACTIVE);
        when(rentalRepository.findById(2L)).thenReturn(Optional.of(r));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> rentalService.cancelRentalRequest(2L)
        );
        assertEquals("Невозможно отменить запрос", ex.getMessage());
    }

    @Test
    void rejectRentalRequest_success() {
        Rental r = new Rental();
        r.setStatus(RentalStatus.PENDING_FOR_CONFIRM);
        r.setOffer(offer);
        r.setRenter(renter);
        when(rentalRepository.findById(3L)).thenReturn(Optional.of(r));

        rentalService.rejectRentalRequest(3L);

        assertEquals(RentalStatus.CANCELED_BY_OWNER, r.getStatus());
        verify(mailSenderService).sendSimpleMail(
                eq("renter@example.com"),
                contains("отклонен"),
                anyString()
        );
        assertEquals(OfferStatus.AVAILABLE, offer.getStatus());
    }

    @Test
    void confirmRentalRequest_success() {
        Rental r = new Rental();
        r.setStatus(RentalStatus.PENDING_FOR_CONFIRM);
        r.setOffer(offer);
        r.setRenter(renter);
        r.setDays(4);
        when(rentalRepository.findById(4L)).thenReturn(Optional.of(r));

        rentalService.confirmRentalRequest(4L);

        assertEquals(RentalStatus.ACTIVE, r.getStatus());
        assertNotNull(r.getStartDate());
        assertNotNull(r.getEndDate());
        assertEquals(OfferStatus.RENTED, offer.getStatus());
        verify(mailSenderService).sendSimpleMail(
                eq("renter@example.com"),
                contains("подтвержден"),
                anyString()
        );
    }

    @Test
    void initiateRentalReturn_success() {
        Rental r = new Rental();
        r.setStatus(RentalStatus.ACTIVE);
        r.setOffer(offer);
        r.setRenter(renter);
        when(rentalRepository.findById(5L)).thenReturn(Optional.of(r));

        rentalService.initiateRentalReturn(5L);

        assertEquals(RentalStatus.PENDING_FOR_RETURN, r.getStatus());
        verify(mailSenderService).sendSimpleMail(
                eq("owner@example.com"),
                contains("Новый запрос на возврат"),
                anyString()
        );
    }

    @Test
    void confirmRentalReturn_success() {
        Rental r = new Rental();
        r.setStatus(RentalStatus.PENDING_FOR_RETURN);
        r.setOffer(offer);
        r.setRenter(renter);
        r.setDays(2);
        when(rentalRepository.findById(6L)).thenReturn(Optional.of(r));

        rentalService.confirmRentalReturn(6L);

        assertEquals(RentalStatus.RETURNED, r.getStatus());
        assertEquals(OfferStatus.AVAILABLE, offer.getStatus());
        verify(mailSenderService).sendSimpleMail(
                eq("renter@example.com"),
                contains("подтвержден"),
                anyString()
        );
    }

    @Test
    void getPendingRequestsForOwner_pages() {
        offer.setOwner(owner);
        Rental r = new Rental();
        r.setOffer(offer);
        r.setStatus(RentalStatus.PENDING_FOR_CONFIRM);
        r.setRenter(renter);
        Page<Rental> page = new PageImpl<>(List.of(r), PageRequest.of(0, 1), 1);
        SecurityContextHolder.getContext()
                .setAuthentication(new TestingAuthenticationToken("ownerUser", null));
        when(rentalRepository.findByOfferOwnerUsernameAndStatus(
                eq("ownerUser"), eq(RentalStatus.PENDING_FOR_CONFIRM), any(PageRequest.class)
        )).thenReturn(page);
        when(modelMapper.map(r, RentalViewModel.class)).thenAnswer(inv -> new RentalViewModel());

        var result = rentalService.getPendingRequestsForOwner(0, 1);
        assertEquals(1, result.getContent().size());
    }

    @Test
    void autoDeclineRentalRequest_processesOld() {
        Rental old = new Rental();
        old.setStatus(RentalStatus.PENDING_FOR_CONFIRM);
        old.setCreated(LocalDateTime.now().minusHours(25));
        old.setOffer(offer);
        when(rentalRepository.findAllByStatusAndCreatedBefore(
                eq(RentalStatus.PENDING_FOR_CONFIRM), any(LocalDateTime.class)
        )).thenReturn(List.of(old));

        rentalService.autoDeclineRentalRequest();

        assertEquals(RentalStatus.CANCELED_BY_SCHEDULER, old.getStatus());
        verify(offerRepository).save(offer);
    }

    @Test
    void getMyRentalsByStatus_pagesCorrectly() {
        Rental r = new Rental();
        r.setRenter(renter);
        r.setStatus(RentalStatus.ACTIVE);
        r.setOffer(offer);
        Page<Rental> page = new PageImpl<>(List.of(r), PageRequest.of(2, 3), 1);
        when(rentalRepository.findByRenterUsernameAndStatus(
                eq(RENTER), eq(RentalStatus.ACTIVE), any(PageRequest.class)
        )).thenReturn(page);
        when(modelMapper.map(r, RentalViewModel.class)).thenAnswer(inv -> new RentalViewModel());

        var result = rentalService.getMyRentalsByStatus(RentalStatus.ACTIVE, 2, 3);
        assertEquals(1, result.getContent().size());
    }

    @Test
    void getActiveRentalsForOwner_pagesCorrectly() {
        Rental r = new Rental();
        r.setOffer(offer);
        r.setRenter(renter);
        r.setStatus(RentalStatus.ACTIVE);
        Page<Rental> page = new PageImpl<>(List.of(r), PageRequest.of(0, 5), 1);
        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken("ownerUser", null)
        );
        when(rentalRepository.findByOfferOwnerUsernameAndStatus(
                eq("ownerUser"), eq(RentalStatus.ACTIVE), any(PageRequest.class)
        )).thenReturn(page);
        when(modelMapper.map(r, RentalViewModel.class)).thenAnswer(inv -> new RentalViewModel());

        var result = rentalService.getActiveRentalsForOwner(0, 5);
        assertEquals(1, result.getContent().size());
    }

    @Test
    void getPendingReturnsForOwner_pagesCorrectly() {
        Rental r = new Rental();
        r.setOffer(offer);
        r.setRenter(renter);
        r.setStatus(RentalStatus.PENDING_FOR_RETURN);
        Page<Rental> page = new PageImpl<>(List.of(r), PageRequest.of(1, 2), 1);
        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken("ownerUser", null)
        );
        when(rentalRepository.findByOfferOwnerUsernameAndStatus(
                eq("ownerUser"), eq(RentalStatus.PENDING_FOR_RETURN), any(PageRequest.class)
        )).thenReturn(page);
        when(modelMapper.map(r, RentalViewModel.class)).thenAnswer(inv -> new RentalViewModel());

        var result = rentalService.getPendingReturnsForOwner(1, 2);
        assertEquals(1, result.getContent().size());
    }

    @Test
    void getCompletedRentalsForOwner_pagesCorrectly() {
        Rental r = new Rental();
        r.setOffer(offer);
        r.setRenter(renter);
        r.setStatus(RentalStatus.RETURNED);
        Page<Rental> page = new PageImpl<>(List.of(r), PageRequest.of(3, 4), 1);
        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken("ownerUser", null)
        );
        when(rentalRepository.findByOfferOwnerUsernameAndStatus(
                eq("ownerUser"), eq(RentalStatus.RETURNED), any(PageRequest.class)
        )).thenReturn(page);
        when(modelMapper.map(r, RentalViewModel.class)).thenAnswer(inv -> new RentalViewModel());

        var result = rentalService.getCompletedRentalsForOwner(3, 4);
        assertEquals(1, result.getContent().size());
    }

}