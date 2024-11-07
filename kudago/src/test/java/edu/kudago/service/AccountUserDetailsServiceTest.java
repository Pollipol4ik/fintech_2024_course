package edu.kudago.service;

import edu.kudago.IntegrationEnvironment;
import edu.kudago.exceptions.AccountAlreadyExistException;
import edu.kudago.exceptions.RoleNotFoundException;
import edu.kudago.model.Role;
import edu.kudago.repository.RoleRepository;
import edu.kudago.repository.UserRepository;
import edu.kudago.repository.entity.RoleEntity;
import edu.kudago.repository.entity.UserEntity;
import edu.kudago.security.CustomUserDetailsService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@DirtiesContext
class AccountUserDetailsServiceTest extends IntegrationEnvironment {

    @Autowired
    private AuthService authService;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleService roleService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    @AfterEach
    void clear() {
        // Очистка данных перед и после каждого теста
        roleRepository.deleteAll();
        jdbcTemplate.execute("ALTER SEQUENCE role_pk_seq RESTART");
        Role[] roles = Role.values();
        for (Role role : roles) {
            RoleEntity roleEntity = new RoleEntity(null, role, null);
            roleService.save(roleEntity);
        }
        userRepository.deleteAll();
        jdbcTemplate.execute("ALTER SEQUENCE users_pk_seq RESTART");
    }

    @DisplayName("Register account")
    @ParameterizedTest(name = "{index} - account register {2}")
    @CsvSource(value = {
            "polina@dmail.ru, Polipol4ik, 141980, Polina, Kuptsova",
            "igor@dmail.ru, Igorek, 123456789, Igor, Frank"
    })
    void registerTest(String email, String nickname, String password, String firstName, String lastName) throws RoleNotFoundException {
        // given
        // when
        authService.register(email, nickname, password, firstName, lastName);

        // then
        var accounts = userRepository.findAll();
        assertEquals(1, accounts.size(), "Account was not created correctly");
        var account = accounts.get(0);
        assertEquals(1, account.getId(), "Account ID is incorrect");
        assertEquals(email, account.getEmail(), "Account email is incorrect");
        assertEquals(nickname, account.getNickname(), "Account nickname is incorrect");
    }

    @DisplayName("Register account with existing email")
    @ParameterizedTest(name = "{index} - account not register {2}")
    @CsvSource(value = {
            "polina@dmail.ru, Polipol4ik, 141980, Polina, Kuptsova",
            "igor@dmail.ru, Igorek, 123456789, Igor, Frank"
    })
    void registerWithEmailThrowTest(String email, String nickname, String password, String firstName, String lastName) throws RoleNotFoundException {
        // given
        var existingUser = new UserEntity(null, email, password, nickname, null, null, roleService.getRoleByName(Role.ROLE_USER));
        userRepository.save(existingUser);

        // when & then
        AccountAlreadyExistException exception = assertThrows(AccountAlreadyExistException.class,
                () -> authService.register(email, nickname, password, firstName, lastName));
        assertEquals("Аккаунт с почтой: " + email + " уже существует", exception.getMessage(), "Exception message is incorrect");
    }

    @DisplayName("Load user by username")
    @ParameterizedTest(name = "{index} - load user by user name {0}")
    @ValueSource(strings = {"dan@dan.ru", "daad@dddd.com"})
    void loadUserByUsernameTest(String email) throws RoleNotFoundException {
        // given
        var accountGiven = new UserEntity(null, email, "123", "asdasda", null, null, roleService.getRoleByName(Role.ROLE_USER));
        userRepository.save(accountGiven);

        // when
        var accountWhen = customUserDetailsService.loadUserByUsername(email);

        // then
        var account = userRepository.findByEmail(email);
        assertTrue(account.isPresent(), "Account should be present in the repository");
        var accountGet = account.get();
        assertEquals(accountWhen.getUsername(), accountGet.getEmail(), "Username mismatch between repository and service");
    }

    @DisplayName("Register admin")
    @Test
    void registerAdminTest() throws RoleNotFoundException {
        // given
        String email = "admin@admin.ru";
        String nickname = "admin";
        String password = "admin";
        String firstName = "admin";
        String lastName = "example";

        // when
        authService.registerAdmin(new UserEntity(null, email, password, nickname, firstName, lastName, roleService.getRoleByName(Role.ROLE_ADMIN)));

        // then
        var accounts = userRepository.findAll();
        assertEquals(1, accounts.size(), "Only one account should be present after admin registration");
        var account = accounts.get(0);
        assertEquals(1, account.getId(), "Admin account ID is incorrect");
        assertEquals(email, account.getEmail(), "Admin email is incorrect");
        assertEquals(nickname, account.getNickname(), "Admin nickname is incorrect");
    }
}
