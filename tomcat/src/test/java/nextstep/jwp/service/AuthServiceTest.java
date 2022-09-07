package nextstep.jwp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.dto.LoginRequest;
import nextstep.jwp.dto.UserDto;
import nextstep.jwp.exception.UserNotFoundException;
import nextstep.jwp.exception.WrongPasswordException;
import nextstep.jwp.model.User;
import org.apache.coyote.session.Session;
import org.apache.coyote.session.SessionManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class AuthServiceTest {

    @Test
    @DisplayName("register 메서드는 유저를 회원가입 시킨다.")
    void register() {
        // given
        final AuthService authService = AuthService.getInstance();

        final String account = "아스피";
        final UserDto request = new UserDto(account, "1234", "아스피@levellog.app");

        // when
        final String actual = authService.register(request);

        // then
        final Optional<User> possibleUser = InMemoryUserRepository.findByAccount(account);

        assertThat(actual).isNotNull();
        assertThat(possibleUser).isPresent();
    }

    @Nested
    @DisplayName("alreadyLogin 메서드는")
    class AlreadyLogin {

        @Test
        @DisplayName("이미 로그인한 경우 true를 반환한다.")
        void alreadyLogin_alreadyLogin_true() {
            // given
            final AuthService authService = AuthService.getInstance();

            final UserDto request = new UserDto("아스피", "1234", "아스피@levellog.app");
            final String sessionId = authService.register(request);

            // when
            final boolean actual = authService.alreadyLogin(sessionId);

            // then
            assertThat(actual).isTrue();
        }

        @Test
        @DisplayName("세션 id에 해당하는 세션이 존재하지 않으면 false를 반환한다.")
        void alreadyLogin_sessionNotFound_false() {
            // given
            final AuthService authService = AuthService.getInstance();

            // when
            final boolean actual = authService.alreadyLogin("fake-session-id");

            // then
            assertThat(actual).isFalse();
        }

        @Test
        @DisplayName("세션에 user가 존재하지 않으면 false를 반환한다.")
        void alreadyLogin_userNotInSession_false() {
            // given
            final AuthService authService = AuthService.getInstance();

            final Session session = Session.generate();
            SessionManager.add(session);

            // when
            final boolean actual = authService.alreadyLogin(session.getId());

            // then
            assertThat(actual).isFalse();
        }

        @Test
        @DisplayName("세션의 user 정보가 DB에 존재하지 않으면 false를 반환한다.")
        void alreadyLogin_userNotInDB_false() {
            // given
            final AuthService authService = AuthService.getInstance();

            final Session session = Session.generate();
            session.setAttribute("user", new User("릭", "1234", "릭@levellog.app"));
            SessionManager.add(session);

            // when
            final boolean actual = authService.alreadyLogin(session.getId());

            // then
            assertThat(actual).isFalse();
        }
    }

    @Nested
    @DisplayName("login 메서드는")
    class Login {

        @Test
        @DisplayName("요청이 유효하면 세션 아이디를 반환한다.")
        void login_validRequest_sessionId() {
            // given
            final AuthService authService = AuthService.getInstance();

            final String account = "rick";
            final String password = "rick123";
            final User expected = new User(account, password, "hello@levellog.app");
            InMemoryUserRepository.save(expected);

            final LoginRequest request = new LoginRequest(account, password);

            // when
            final String sessionId = authService.login(request);

            // then
            final User actual = (User) SessionManager.findSession(sessionId)
                    .getAttribute("user");

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        @DisplayName("존재하지 않는 유저이면 예외를 던진다.")
        void login_userNotExist_exception() {
            // given
            final AuthService authService = AuthService.getInstance();

            final LoginRequest request = new LoginRequest("ghost", "ghost123");

            // when & then
            assertThatThrownBy(() -> authService.login(request))
                    .isInstanceOf(UserNotFoundException.class);
        }

        @Test
        @DisplayName("비밀번호가 불일치하면 예외를 던진다.")
        void login_wrongPassword_exception() {
            // given
            final AuthService authService = AuthService.getInstance();

            final String account = "levellog";
            InMemoryUserRepository.save(new User(account, "1q2w3e4r", "levellog@levellog.app"));

            final LoginRequest request = new LoginRequest(account, "levellog123");

            // when & then
            assertThatThrownBy(() -> authService.login(request))
                    .isInstanceOf(WrongPasswordException.class);
        }
    }
}
