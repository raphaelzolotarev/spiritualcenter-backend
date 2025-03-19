package be.spiritualcenter.service.implementation;

/*
 * @author Raphael Zolotarev
 * @version 1.0
 * @license Copyright (c) 2025 www.zolotarev.eu
 * @since 03/03/2025
 */

import be.spiritualcenter.domain.User;
import be.spiritualcenter.dto.UserDTO;
import be.spiritualcenter.enums.Role;
import be.spiritualcenter.exception.APIException;
import be.spiritualcenter.repository.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepo userRepo;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private UserDTO expectedUserDTO;
    private final int userId = 1;

    @BeforeEach
    void setUp() {
        // Créer un utilisateur de test
        testUser = new User();
        testUser.setId(userId);
        testUser.setUsername("bobby");
        testUser.setEmail("bobby@gmail.com");
        testUser.setPhone("0491234567");
        testUser.setPassword("password");
        testUser.setEnabled(true);
        testUser.setNotLocked(true);
        testUser.setUsingMfa(false);
        testUser.setRole(Role.USER);
        testUser.setPicture("https://upload.wikimedia.org/wikipedia/commons/c/c4/Bruxelles_Manneken_Pis_cropped.jpg");

        expectedUserDTO = new UserDTO();
        expectedUserDTO.setId(userId);
        expectedUserDTO.setUsername("bobby");
        expectedUserDTO.setEmail("bobby@gmail.com");
        expectedUserDTO.setPhone("0491234567");
        expectedUserDTO.setEnabled(true);
        expectedUserDTO.setNotLocked(true);
        expectedUserDTO.setUsingMfa(false);
        expectedUserDTO.setRole(Role.USER);
        expectedUserDTO.setPicture("https://upload.wikimedia.org/wikipedia/commons/c/c4/Bruxelles_Manneken_Pis_cropped.jpg");
    }

    @Test
    void getUserById_ShouldReturnUserDTO_WhenUserExists() {
        // Arrange
        when(userRepo.get(userId)).thenReturn(testUser);

        // Act
        UserDTO actualUserDTO = userService.getUserById(userId);

        // Assert
        assertNotNull(actualUserDTO);
        assertEquals(expectedUserDTO.getId(), actualUserDTO.getId());
        assertEquals(expectedUserDTO.getUsername(), actualUserDTO.getUsername());
        assertEquals(expectedUserDTO.getEmail(), actualUserDTO.getEmail());
        assertEquals(expectedUserDTO.getPhone(), actualUserDTO.getPhone());
        assertEquals(expectedUserDTO.isEnabled(), actualUserDTO.isEnabled());
        assertEquals(expectedUserDTO.isNotLocked(), actualUserDTO.isNotLocked());
        assertEquals(expectedUserDTO.isUsingMfa(), actualUserDTO.isUsingMfa());
        assertEquals(expectedUserDTO.getRole(), actualUserDTO.getRole());
        assertEquals(expectedUserDTO.getPicture(), actualUserDTO.getPicture());

        // Verify
        verify(userRepo, times(1)).get(userId);
    }

    @Test
    void getUserById_ShouldThrowAPIException_WhenUserDoesNotExist() {
        when(userRepo.get(userId)).thenThrow(new APIException("No User found by id: " + userId));

        APIException exception = assertThrows(APIException.class, () -> {
            userService.getUserById(userId);
        });

        assertEquals("No User found by id: " + userId, exception.getMessage());

        verify(userRepo, times(1)).get(userId);
    }
}
