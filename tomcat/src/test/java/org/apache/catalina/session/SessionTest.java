package org.apache.catalina.session;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("세션")
class SessionTest {
    private static final long NOW = 1_000L;
    private static final long THIRTY_MINUTES = Duration.ofMinutes(30).toMillis();

    @Test
    @DisplayName("주어진 아이디를 그대로 가진다")
    void keepGivenId() {
        // expect
        assertThat(new Session("abc123", NOW).getId()).isEqualTo("abc123");
    }

    @Test
    @DisplayName("생성할 때마다 다른 아이디를 만든다")
    void createWithUniqueId() {
        // when
        final Session first = Session.create(NOW);
        final Session second = Session.create(NOW);

        // then
        assertThat(first.getId()).isNotBlank();
        assertThat(first.getId()).isNotEqualTo(second.getId());
    }

    @Test
    @DisplayName("속성을 담고 꺼낸다")
    void storeAttribute() {
        // given
        final Session session = Session.create(NOW);

        // when
        session.setAttribute("user", "gugu");

        // then
        assertThat(session.getAttribute("user")).isEqualTo("gugu");
    }

    @Test
    @DisplayName("담지 않은 속성은 null을 반환한다")
    void nullWhenAttributeIsAbsent() {
        // expect
        assertThat(Session.create(NOW).getAttribute("user")).isNull();
    }

    @Test
    @DisplayName("속성을 지운다")
    void removeAttribute() {
        // given
        final Session session = Session.create(NOW);
        session.setAttribute("user", "gugu");

        // when
        session.removeAttribute("user");

        // then
        assertThat(session.getAttribute("user")).isNull();
    }

    @Test
    @DisplayName("null을 담으면 속성을 지운다")
    void removeAttributeWhenValueIsNull() {
        // given
        final Session session = Session.create(NOW);
        session.setAttribute("user", "gugu");

        // when
        session.setAttribute("user", null);

        // then
        assertThat(session.getAttribute("user")).isNull();
    }

    @Test
    @DisplayName("담긴 속성 이름을 모두 알려준다")
    void listAttributeNames() {
        // given
        final Session session = Session.create(NOW);
        session.setAttribute("user", "gugu");
        session.setAttribute("theme", "dark");

        // when
        final List<String> names = Collections.list(session.getAttributeNames());

        // then
        assertThat(names).containsExactlyInAnyOrder("user", "theme");
    }

    @Test
    @DisplayName("생성 시각을 마지막 접근 시각으로 기록한다")
    void recordCreationTime() {
        // when
        final Session session = Session.create(NOW);

        // then
        assertThat(session.getCreationTime()).isEqualTo(NOW);
        assertThat(session.getLastAccessedTime()).isEqualTo(NOW);
    }

    @Test
    @DisplayName("접근하면 마지막 접근 시각만 갱신한다")
    void updateLastAccessedTimeOnAccess() {
        // given
        final Session session = Session.create(NOW);

        // when
        session.access(NOW + 500);

        // then
        assertThat(session.getCreationTime()).isEqualTo(NOW);
        assertThat(session.getLastAccessedTime()).isEqualTo(NOW + 500);
    }

    @Test
    @DisplayName("기본 비활성 유지 시간은 30분이다")
    void defaultMaxInactiveIntervalIsThirtyMinutes() {
        // expect
        assertThat(Session.create(NOW).getMaxInactiveInterval()).isEqualTo(30 * 60);
    }

    @Test
    @DisplayName("비활성 유지 시간이 지나면 만료된다")
    void expireAfterMaxInactiveInterval() {
        // given
        final Session session = Session.create(NOW);

        // expect
        assertThat(session.isExpired(NOW + THIRTY_MINUTES - 1)).isFalse();
        assertThat(session.isExpired(NOW + THIRTY_MINUTES)).isTrue();
    }

    @Test
    @DisplayName("만료 여부는 마지막 접근 시각부터 계산한다")
    void expireFromLastAccessedTime() {
        // given
        final Session session = Session.create(NOW);
        final long tenMinutes = Duration.ofMinutes(10).toMillis();

        // when
        session.access(NOW + tenMinutes);

        // then
        assertThat(session.isExpired(NOW + THIRTY_MINUTES)).isFalse();
        assertThat(session.isExpired(NOW + tenMinutes + THIRTY_MINUTES)).isTrue();
    }

    @Test
    @DisplayName("비활성 유지 시간을 바꿀 수 있다")
    void changeMaxInactiveInterval() {
        // given
        final Session session = Session.create(NOW);

        // when
        session.setMaxInactiveInterval(60);

        // then
        assertThat(session.getMaxInactiveInterval()).isEqualTo(60);
        assertThat(session.isExpired(NOW + Duration.ofSeconds(59).toMillis())).isFalse();
        assertThat(session.isExpired(NOW + Duration.ofSeconds(60).toMillis())).isTrue();
    }

    @Test
    @DisplayName("비활성 유지 시간이 0 이하면 만료되지 않는다")
    void neverExpireWhenIntervalIsNotPositive() {
        // given
        final Session zero = Session.create(NOW);
        final Session negative = Session.create(NOW);
        final long oneYearLater = NOW + Duration.ofDays(365).toMillis();

        // when
        zero.setMaxInactiveInterval(0);
        negative.setMaxInactiveInterval(-1);

        // then
        assertThat(zero.isExpired(oneYearLater)).isFalse();
        assertThat(negative.isExpired(oneYearLater)).isFalse();
    }

    @Test
    @DisplayName("구현하지 않은 기능은 예외를 던진다")
    void throwOnUnsupportedOperation() {
        // given
        final Session session = Session.create(NOW);

        // expect
        assertThatThrownBy(session::invalidate).isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(session::getServletContext).isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(session::isNew).isInstanceOf(UnsupportedOperationException.class);
    }
}
