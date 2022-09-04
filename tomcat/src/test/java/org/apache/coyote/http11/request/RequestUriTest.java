package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.NoSuchElementException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("RequestUri 클래스의")
class RequestUriTest {

    @Nested
    @DisplayName("생성자는")
    class Constructor {

        @Test
        @DisplayName("주어진 문자열로 객체를 생성한다.")
        void success() {
            // given
            final String uri = "/index.html";

            // when
            final RequestUri requestUri = RequestUri.from(uri);

            // then
            assertAll(
                    () -> assertThat(requestUri.getPath()).isEqualTo(uri),
                    () -> assertThat(requestUri.getParams()).isEmpty()
            );
        }

        @Test
        @DisplayName("주어진 문자열에 Query Parameter가 존재하는 경우 Path와 구분하여 객체를 생성한다.")
        void successWithQueryParameters() {
            // given
            final String uri = "/login?account=gugu&password=password";

            // when
            final RequestUri requestUri = RequestUri.from(uri);

            // then
            assertAll(
                    () -> assertThat(requestUri.getPath()).isEqualTo("/login"),
                    () -> assertThat(requestUri.getParams()).contains(
                            entry("account", "gugu"),
                            entry("password", "password")
                    )
            );
        }

        @Test
        @DisplayName("Query Parameter가 올바른 형식이 아닌 경우 예외를 던진다.")
        void invalidQueryParameter_ExceptionThrown() {
            // given
            final String uri = "/login?account:gugu";

            // when & then
            assertThatThrownBy(() -> RequestUri.from(uri))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("올바른 Query Parameter 형식이 아닙니다.");
        }
    }

    @Nested
    @DisplayName("findQueryValue 메서드는")
    class FindQueryValue {

        @Test
        @DisplayName("Query Parameter의 key값에 해당하는 value를 반환한다.")
        void success() {
            // given
            final String uri = "/login?account=gugu&password=password";
            final RequestUri requestUri = RequestUri.from(uri);

            // when
            final String actual = requestUri.findQueryValue("account");

            // then
            assertThat(actual).isEqualTo("gugu");
        }

        @Test
        @DisplayName("Query Parameter의 key값에 해당하는 value가 없는 경우 예외를 던진다.")
        void invalidKey_ExceptionThrown() {
            // given
            final String uri = "/login?account=gugu";
            final RequestUri requestUri = RequestUri.from(uri);

            // when & then
            assertThatThrownBy(() -> requestUri.findQueryValue("password"))
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessage("Query Parameter에 key 값이 존재하지 않습니다.");
        }
    }

    @Nested
    @DisplayName("containsQuery 메서드는")
    class ContainsQuery {

        @Test
        @DisplayName("RequestUri에 Query Parameter가 포함되어있는 경우 true를 반환한다.")
        void queryExists() {
            // given
            final String uri = "/login?account=gugu&password=password";
            final RequestUri requestUri = RequestUri.from(uri);

            // when
            final boolean actual = requestUri.containsQuery();

            // then
            assertTrue(actual);
        }

        @Test
        @DisplayName("RequestUri에 Query Parameter가 포함되지 않은 경우 false를 반환한다.")
        void queryNotExists() {
            // given
            final String uri = "/login.html";
            final RequestUri requestUri = RequestUri.from(uri);

            // when
            final boolean actual = requestUri.containsQuery();

            // then
            assertFalse(actual);
        }
    }
}
