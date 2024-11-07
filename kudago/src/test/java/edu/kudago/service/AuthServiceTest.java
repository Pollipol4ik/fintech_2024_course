package edu.kudago.service;

import edu.kudago.IntegrationEnvironment;
import edu.kudago.exceptions.AccountAlreadyExistException;
import edu.kudago.exceptions.LoginFailException;
import edu.kudago.exceptions.RoleNotFoundException;
import edu.kudago.model.Role;
import edu.kudago.repository.RoleRepository;
import edu.kudago.repository.UserRepository;
import edu.kudago.repository.entity.RoleEntity;
import edu.kudago.repository.entity.UserEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@DirtiesContext
class AuthServiceTest extends IntegrationEnvironment {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleService roleService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    @AfterEach
    void clear() {
        userRepository.deleteAll();
        jdbcTemplate.execute("ALTER SEQUENCE users_pk_seq RESTART");
        roleRepository.deleteAll();
        jdbcTemplate.execute("ALTER SEQUENCE role_pk_seq RESTART");
    }

    @DisplayName("Login account")
    @Test
    void loginTest() throws RoleNotFoundException {
        // given
        insertRoles();
        UserEntity user = new UserEntity(null, "user@user.ru", passwordEncoder.encode("12345"), "user", null, null, roleService.getRoleByName(Role.ROLE_USER));
        userRepository.save(user);

        // when
        var result = authService.login("user@user.ru", "12345", false);

        // then
        assertNotNull(result);
        assertEquals("user@user.ru", result.getFirst().getEmail());
        assertNotNull(result.getSecond()); // Проверка, что токен не null
    }

    @DisplayName("Login with incorrect password throws LoginFailException")
    @Test
    void loginWithIncorrectPasswordTest() {
        // given
        insertRoles();
        userRepository.save(new UserEntity(null, "user@user.ru", passwordEncoder.encode("12345"), "user", null, null, roleService.getRoleByName(Role.ROLE_USER)));

        // when & then
        assertThrows(LoginFailException.class, () -> authService.login("user@user.ru", "wrongPassword", false));
    }

    @DisplayName("Register account")
    @Test
    void registerTest() throws AccountAlreadyExistException, RoleNotFoundException {
        // given
        insertRoles();

        // when
        var result = authService.register("newuser@user.ru", "newuser", "password", "First", "Last");

        // then
        var user = userRepository.findByEmail("newuser@user.ru");
        assertTrue(user.isPresent());
        assertEquals("newuser@user.ru", user.get().getEmail());
        assertNotNull(result.getSecond()); // Проверка, что токен не null
    }

    @DisplayName("Register account with existing email throws AccountAlreadyExistException")
    @Test
    void registerWithExistingEmailTest() throws RoleNotFoundException {
        // given
        insertRoles();
        userRepository.save(new UserEntity(null, "user@user.ru", passwordEncoder.encode("password"), "user", null, null, roleService.getRoleByName(Role.ROLE_USER)));

        // when & then
        assertThrows(AccountAlreadyExistException.class, () -> authService.register("user@user.ru", "newuser", "password", "First", "Last"));
    }

    @DisplayName("Register account with existing nickname throws AccountAlreadyExistException")
    @Test
    void registerWithExistingNicknameTest() throws RoleNotFoundException {
        // given
        insertRoles();
        userRepository.save(new UserEntity(null, "user@user.ru", passwordEncoder.encode("password"), "newuser", null, null, roleService.getRoleByName(Role.ROLE_USER)));

        // when & then
        assertThrows(AccountAlreadyExistException.class, () -> authService.register("newuser2@user.ru", "newuser", "password", "First", "Last"));
    }

    @DisplayName("Reset password with correct code")
    @Test
    void resetPasswordTest() throws RoleNotFoundException {
        // given
        insertRoles();
        UserEntity user = new UserEntity(null, "reset@user.ru", passwordEncoder.encode("oldpassword"), "user", null, null, roleService.getRoleByName(Role.ROLE_USER));
        userRepository.save(user);

        // when
        authService.resetPassword("reset@user.ru", "newpassword", "0000");

        // then
        var updatedUser = userRepository.findByEmail("reset@user.ru");
        assertTrue(updatedUser.isPresent());
        assertTrue(passwordEncoder.matches("newpassword", updatedUser.get().getPassword()));
    }

    @DisplayName("Reset password with incorrect code throws IllegalArgumentException")
    @Test
    void resetPasswordWithIncorrectCodeTest() throws RoleNotFoundException {
        // given
        insertRoles();
        userRepository.save(new UserEntity(null, "reset@user.ru", passwordEncoder.encode("oldpassword"), "user", null, null, roleService.getRoleByName(Role.ROLE_USER)));

        // when & then
        assertThrows(IllegalArgumentException.class, () -> authService.resetPassword("reset@user.ru", "newpassword", "wrongcode"));
    }

    @DisplayName("Register admin account")
    @Test
    void registerAdminTest() throws AccountAlreadyExistException, RoleNotFoundException {
        // given
        insertRoles();
        UserEntity admin = new UserEntity(null, "admin@admin.ru", "admin", "admin", "admin", "admin", roleService.getRoleByName(Role.ROLE_ADMIN));

        // when
        authService.registerAdmin(admin);

        // then
        var savedAdmin = userRepository.findByEmail("admin@admin.ru");
        assertTrue(savedAdmin.isPresent());
        assertEquals("admin@admin.ru", savedAdmin.get().getEmail());
    }

    private void insertRoles() throws RoleNotFoundException {
        for (Role role : Role.values()) {
            if (!roleService.roleExists(role)) {
                RoleEntity roleEntity = new RoleEntity(null, role, null);
                roleService.save(roleEntity);
            }
        }
    }
}
