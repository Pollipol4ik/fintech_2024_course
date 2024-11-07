package edu.kudago.service;


import edu.kudago.exceptions.AccountAlreadyExistException;
import edu.kudago.exceptions.LoginFailException;
import edu.kudago.exceptions.RoleNotFoundException;
import edu.kudago.model.Role;
import edu.kudago.repository.RoleRepository;
import edu.kudago.repository.UserRepository;
import edu.kudago.repository.entity.RoleEntity;
import edu.kudago.repository.entity.UserEntity;
import edu.kudago.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    private static final String TWO_FACTOR_CODE = "0000";
    private static final Integer MONTH_LIVE_TOKEN = 30 * 24 * 60 * 60;
    private static final Integer TEN_MINUTES_LIVE_TOKEN = 10 * 60;

    @Transactional
    public Pair<UserEntity, String> login(String email, String password, boolean rememberMe) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );

            UserEntity account = userRepository.findByEmail(email).orElseThrow(LoginFailException::new);

            int tokenExpiration = rememberMe ? MONTH_LIVE_TOKEN : TEN_MINUTES_LIVE_TOKEN;
            String token = jwtService.generateToken(account, tokenExpiration);
            return Pair.of(account, token);
        } catch (AuthenticationException e) {
            throw new LoginFailException();
        }
    }


    @Transactional
    public Pair<UserEntity, String> register(String email, String nickname, String password, String firstName, String lastName) throws AccountAlreadyExistException {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new AccountAlreadyExistException("почтой", email);
        }
        if (userRepository.findByNickname(nickname).isPresent()) {
            throw new AccountAlreadyExistException("ником", nickname);
        }
        var account = new UserEntity();
        account.setEmail(email);
        account.setNickname(nickname);
        account.setPassword(passwordEncoder.encode(password));
        account.setFirstName(firstName);
        account.setLastName(lastName);
        account.setRole(getRoleByName(Role.ROLE_USER));
        UserEntity savedAccount = userRepository.save(account);
        String token = jwtService.generateToken(savedAccount, TEN_MINUTES_LIVE_TOKEN);
        return Pair.of(savedAccount, token);
    }


    public RoleEntity getRoleByName(Role role) throws RoleNotFoundException {
        return roleRepository.findByName(role).orElseThrow(() -> new RoleNotFoundException(role));
    }


    @Transactional
    public void resetPassword(String email, String newPassword, String code) {
        if (!TWO_FACTOR_CODE.equals(code)) {
            throw new IllegalArgumentException("Неверный код подтверждения");
        }

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь с указанным email не найден"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Transactional
    public void registerAdmin(UserEntity userEntity) throws AccountAlreadyExistException {
        if (userRepository.findByEmail(userEntity.getEmail()).isEmpty()) {
            userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
            userRepository.save(userEntity);
        }
    }
}
