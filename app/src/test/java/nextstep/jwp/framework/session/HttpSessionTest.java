package nextstep.jwp.framework.session;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class HttpSessionTest {

    @DisplayName("HttpSession 을 생성한다.")
    @Test
    void create() throws InterruptedException {
        // given
        String sessionId = "sessionId";

        // when
        HttpSession httpSession = new HttpSession(sessionId);

        // then
        Thread.sleep(1);
        assertThat(httpSession.getId()).isEqualTo(sessionId);
        assertThat(httpSession.getAccessTime()).isLessThan(System.currentTimeMillis());
    }

    @DisplayName("HttpSession 에 값을 넣고 가져온다.")
    @Test
    void putAndTake() {
        // given
        HttpSession httpSession = new HttpSession("sessionId");

        String intName = "int";
        int intValue = 10000;

        String stringName = "string";
        String stringValue = "hello";

        String listName = "list";
        List<String> listValue = new ArrayList<>();

        String userName = "user";
        MockUser userValue = new MockUser("1", "ggyool");

        // when, then
        httpSession.put(intName, intValue);
        httpSession.put(stringName, stringValue);
        httpSession.put(listName, listValue);
        httpSession.put(userName, userValue);

        assertThat(httpSession.take(intName)).get().isEqualTo(intValue);
        assertThat(httpSession.take(stringName)).get().isEqualTo(stringValue);
        assertThat(httpSession.take(listName)).get().isSameAs(listValue);
        assertThat(httpSession.take(userName)).get().isSameAs(userValue);
    }

    @DisplayName("HttpSession 에서 없는 값을 가져온다.")
    @Test
    void takeNonexistentName() {
        HttpSession httpSession = new HttpSession("sessionId");
        assertThat(httpSession.take("abc"))
                .isInstanceOf(Optional.class)
                .isEmpty();
    }

    @DisplayName("HttpSession 에서 값을 삭제한다.")
    @Test
    void remove() {
        // given
        String sessionId = "sessionId";
        HttpSession httpSession = new HttpSession(sessionId);

        String name = "user";
        MockUser value = new MockUser("1", "ggyool");
        httpSession.put(name, value);
        assertThat(httpSession.take(name)).isNotEmpty();

        // when
        httpSession.remove(name);

        // then
        assertThat(httpSession.take(name)).isEmpty();
    }

    @DisplayName("세션 저장소에서 해당 세션을 삭제한다.")
    @Test
    void invalidate() {
        // given
        String sessionId = "sessionId";
        HttpSession httpSession = new HttpSession(sessionId);

        HttpSessions.add(sessionId, httpSession);
        assertThat(HttpSessions.find(sessionId)).isNotEmpty();

        // when
        httpSession.invalidate();

        // then
        assertThat(HttpSessions.find(sessionId)).isEmpty();
    }

    @DisplayName("Access Time 을 새로고침 한다.")
    @Test
    void refreshAccessTime() throws InterruptedException {
        // given
        HttpSession httpSession = new HttpSession("sessionId");
        long beforeTime = httpSession.getAccessTime();
        Thread.sleep(1);

        // when
        httpSession.refreshAccessTime();

        // then
        assertThat(httpSession.getAccessTime()).isGreaterThan(beforeTime);
    }

    @DisplayName("만료된 세션이다.")
    @Test
    void isExpiredTrue() throws InterruptedException {
        // given
        HttpSession httpSession = new HttpSession("sessionId", 0);
        Thread.sleep(1);

        // when, then
        assertThat(httpSession.isExpired()).isTrue();
    }

    @DisplayName("만료된 세션이 아니다. (기본 만료 기간 30분)")
    @Test
    void isExpiredFalse() {
        // given
        HttpSession httpSession = new HttpSession("sessionId");

        assertThat(httpSession.isExpired()).isFalse();
    }

    private static class MockUser {

        private final String id;
        private final String account;

        public MockUser(String id, String account) {
            this.id = id;
            this.account = account;
        }
    }
}
