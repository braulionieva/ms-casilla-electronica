package pe.gob.mpfn.casilla.notifications.service;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import pe.gob.mpfn.casilla.notifications.event.email.UpdatePasswordEvent;
import pe.gob.mpfn.casilla.notifications.exception.AffiliateNotFoundException;
import pe.gob.mpfn.casilla.notifications.model.dto.AccountRecord;
import pe.gob.mpfn.casilla.notifications.model.dto.UpdatePasswordRequest;
import pe.gob.mpfn.casilla.notifications.model.dto.UpdatePasswordResponse;
import pe.gob.mpfn.casilla.notifications.model.dto.ValidateCodeResponse;
import pe.gob.mpfn.casilla.notifications.repository.AffiliateRepository;
import pe.gob.mpfn.casilla.notifications.security.AuthenticationRequest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class AccountServiceTest {

    @Mock
    private AffiliateRepository affiliateRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private AccountService accountService;

    @Test
    void testUpdatePasswordSuccess() {
        // Arrange
        String dni = "12345678";
        String oldPassword = "oldPassword";
        String newPassword = "newPassword";
        UpdatePasswordRequest request = new UpdatePasswordRequest(dni, newPassword, oldPassword, "");
        AuthenticationRequest authRequest = new AuthenticationRequest(dni, oldPassword, "");
        UpdatePasswordResponse updateResponse = new UpdatePasswordResponse("", "pcruzd@mpfn.gob.pe");

        when(affiliateRepository.getAccountData(authRequest)).thenReturn(Optional.of(new AccountRecord("pcruzd@mpfn.gob.pe", "1223123", "Nombre", "App", "Mat", "", "")));
        when(affiliateRepository.updatePassword(any(UpdatePasswordRequest.class))).thenReturn(updateResponse);

        // Act
        ValidateCodeResponse response = accountService.updatePassword(dni, request);

        // Assert
        verify(affiliateRepository, times(1)).getAccountData(authRequest);
        verify(affiliateRepository, times(1)).updatePassword(any(UpdatePasswordRequest.class));
        verify(eventPublisher, times(1)).publishEvent(any(UpdatePasswordEvent.class));
        assertEquals(new ValidateCodeResponse(), response);
    }

    @Test
    void testUpdatePasswordInvalidCredentials() {
        // Arrange
        String dni = "12345678";
        String oldPassword = "wrongPassword";
        String newPassword = "newPassword";
        UpdatePasswordRequest request = new UpdatePasswordRequest(dni, newPassword, oldPassword, "");
        AuthenticationRequest authRequest = new AuthenticationRequest(dni, oldPassword, "");

        when(affiliateRepository.getAccountData(authRequest)).thenReturn(Optional.empty());
        assertThrows(AffiliateNotFoundException.class, () -> accountService.updatePassword(dni, request));

        // Act & Assert
        verify(affiliateRepository, times(1)).getAccountData(authRequest);
        verify(affiliateRepository, never()).updatePassword(any(UpdatePasswordRequest.class));
        verify(eventPublisher, never()).publishEvent(any(UpdatePasswordEvent.class));
    }
}