package nextstep.jwp.dashboard.service;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import nextstep.jwp.dashboard.controller.dto.UserDto;
import nextstep.jwp.dashboard.domain.User;
import nextstep.jwp.dashboard.repository.InMemoryUserRepository;
import nextstep.jwp.httpserver.exception.AuthorizationException;
import nextstep.jwp.httpserver.exception.DuplicatedException;
import nextstep.jwp.httpserver.exception.NotFoundException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;


@DisplayName("UserService 단위 테스트")
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private InMemoryUserRepository inMemoryUserRepository;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("로그인 성공")
    void loginSuccess() {
        // given
        String account = "air";
        String password = "1234";
        String email = "air.junseo@gmail.com";
        given(inMemoryUserRepository.findByAccount(account))
                .willReturn(Optional.of(new User(1L, account, password, email)));

        // when
        UserDto userDto = userService.login(account, password);

        // then
        assertThat(userDto.getId()).isEqualTo(1L);
        assertThat(userDto.getAccount()).isEqualTo(account);
        assertThat(userDto.getPassword()).isEqualTo(password);
        assertThat(userDto.getEmail()).isEqualTo(email);
    }

    @Test
    @DisplayName("존재하지 않는 유저 아이디로 로그인 시도하는 경우 404 예외가 발생한다.")
    void notExistUserLogin() {
        // given
        String account = "air";
        String password = "1234";
        given(inMemoryUserRepository.findByAccount(account))
                .willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> userService.login(account, password))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("잘못된 비밀번호로 로그인 시도하는 경우 401 예외가 발생한다.")
    void wrongPassword() {
        // given
        String account = "air";
        String password = "1234";
        String email = "air.junseo@gmail.com";
        given(inMemoryUserRepository.findByAccount(account))
                .willReturn(Optional.of(new User(1L, account, password, email)));

        // when
        // then
        assertThatThrownBy(() -> userService.login(account, password + "1"))
                .isInstanceOf(AuthorizationException.class);
    }

    @Test
    @DisplayName("회원 가입 성공")
    void joinSuccess() {
        // given
        String account = "air";
        String password = "1234";
        String email = "air.junseo@gmail.com";
        given(inMemoryUserRepository.findByAccount(account))
                .willReturn(Optional.empty());

        // when
        userService.join(account, password, email);

        // then
        verify(inMemoryUserRepository).save(any());
    }

    @Test
    @DisplayName("이미 가입된 유저 아이디로 회원가입을 시도하는 경우 400 예외가 발생한다.")
    void alreadyJoin() {
        // given
        String account = "air";
        String password = "1234";
        String email = "air.junseo@gmail.com";
        given(inMemoryUserRepository.findByAccount(account))
                .willReturn(Optional.of(new User(1L, account, password, email)));

        // when
        // then
        assertThatThrownBy(() -> userService.join(account, password, email))
                .isInstanceOf(DuplicatedException.class);
    }
}
