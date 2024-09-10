package org.apache.coyote.http11.protocol.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestURITest {

    @Test
    @DisplayName("RequestURI 를 생성한다.")
    void createRequestURI() {
        String requestURIString = "/index.html?name=lee&age=20";
        String expectedPath = "/index.html";
        Map<String, String> expectedQueryParameters = Map.of("name", "lee", "age", "20");

        RequestURI requestURI = new RequestURI(requestURIString);

        assertAll(
                () -> assertThat(requestURI.getPath()).isEqualTo(expectedPath),
                () -> assertThat(requestURI.getQueryParameters())
                        .containsExactlyInAnyOrderEntriesOf(expectedQueryParameters)
        );
    }

    @Test
    @DisplayName("쿼리스트링이 없는 RequestURI 를 생성한다.")
    void createRequestURIWithoutQueryString() {
        String requestURIString = "/index.html";
        String expectedPath = "/index.html";

        RequestURI requestURI = new RequestURI(requestURIString);

        assertAll(
                () -> assertThat(requestURI.getPath()).isEqualTo(expectedPath),
                () -> assertThat(requestURI.getQueryParameters()).isEmpty()

        );
    }

    @Test
    @DisplayName("빈 URI 를 처리한다.")
    void createRequestURIWithEmptyURI() {
        String requestURIString = "";
        String expectedPath = "";

        RequestURI requestURI = new RequestURI(requestURIString);

        assertAll(
                () -> assertThat(requestURI.getPath()).isEqualTo(expectedPath),
                () -> assertThat(requestURI.getQueryParameters()).isEmpty()
        );
    }

    @Test
    @DisplayName("쿼리스트링이 있지만 값이 없는 경우를 처리한다.")
    void createRequestURIWithEmptyQueryString() {
        String requestURIString = "/index.html?";
        String expectedPath = "/index.html";

        RequestURI requestURI = new RequestURI(requestURIString);

        assertAll(
                () -> assertThat(requestURI.getPath()).isEqualTo(expectedPath),
                () -> assertThat(requestURI.getQueryParameters()).isEmpty()
        );
    }

    @Test
    @DisplayName("잘못된 형식의 URI 를 처리한다.")
    void createRequestURIWithMalformedQueryString() {
        String requestURIString = "/index.html?name=lee&age";
        String expectedPath = "/index.html";
        Map<String, String> expectedQueryParameters = Map.of("name", "lee");

        RequestURI requestURI = new RequestURI(requestURIString);

        assertAll(
                () -> assertThat(requestURI.getPath()).isEqualTo(expectedPath),
                () -> assertThat(requestURI.getQueryParameters())
                        .containsExactlyInAnyOrderEntriesOf(expectedQueryParameters)
        );
    }
}
