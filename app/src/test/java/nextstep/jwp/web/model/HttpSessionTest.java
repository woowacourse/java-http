package nextstep.jwp.web.model;

import nextstep.jwp.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HttpSessionTest {

    @DisplayName("create(): HttpSession 객체 생성할 수 있다.")
    @Test
    void create() {
        // when
        HttpSession httpSession = HttpSession.create();

        // then
        assertThat(httpSession.getId()).isNotNull();
    }

    @DisplayName("세션안에 다양한 타입의 값을 저장할 수 있다.")
    @Test
    void sessionValue() {
        // given
        HttpSession httpSession = HttpSession.create();
        String stringType = "String";
        String longType = "Long";
        String userType = "User";

        // when
        String stringValue = "value";
        httpSession.setAttribute(stringType, stringValue);

        long longValue = 100L;
        httpSession.setAttribute(longType, longValue);

        User user = new User(1L, "charlie", "1234", "test@test.com");
        httpSession.setAttribute(userType, user);

        // then
        String attribute1 = (String) httpSession.getAttribute(stringType);
        Long attribute2 = (Long) httpSession.getAttribute(longType);
        User attribute3 = (User) httpSession.getAttribute(userType);

        assertThat(attribute1).isEqualTo(stringValue);
        assertThat(attribute2).isEqualTo(longValue);
        assertThat(attribute3).isEqualTo(user);
    }

    @DisplayName("removeAttribute(): 세션 내부에 저장된 키를 이용해서 값을 제거한다.")
    @Test
    void removeAttribute() {
        // given
        HttpSession httpSession = HttpSession.create();
        String stringType = "String";
        String stringValue = "value";
        httpSession.setAttribute(stringType, stringValue);

        // when
        httpSession.removeAttribute(stringType);

        // then
        Object attribute = httpSession.getAttribute(stringType);
        assertThat(attribute).isNull();
    }

    @DisplayName("invalidate(): 세션 내부에 저장된 값을 모두 제거할 수 있다.")
    @Test
    void invalidate() {
        // given
        HttpSession httpSession = HttpSession.create();
        String stringType = "String";
        String longType = "Long";
        String userType = "User";
        String stringValue = "value";
        long longValue = 100L;
        User user = new User(1L, "charlie", "1234", "test@test.com");
        httpSession.setAttribute(longType, longValue);
        httpSession.setAttribute(stringType, stringValue);
        httpSession.setAttribute(userType, user);

        // when
        httpSession.invalidate();

        // then
        String attribute1 = (String) httpSession.getAttribute(stringType);
        Long attribute2 = (Long) httpSession.getAttribute(longType);
        User attribute3 = (User) httpSession.getAttribute(userType);

        assertThat(attribute1).isNull();
        assertThat(attribute2).isNull();
        assertThat(attribute3).isNull();
    }
}