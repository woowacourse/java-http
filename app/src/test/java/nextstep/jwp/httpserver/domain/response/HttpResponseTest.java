package nextstep.jwp.httpserver.domain.response;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.jwp.httpserver.domain.HttpVersion;
import nextstep.jwp.httpserver.domain.StatusCode;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("HttpResponse 단위 테스트")
class HttpResponseTest {

    @Test
    @DisplayName("HttpResponse 빌더 패턴 테스트")
    void builder() {
        // given
        Map<String, String> body = new HashMap<>();
        body.put("name", "air");

        // when
        HttpResponse httpResponse = new HttpResponse.Builder()
                .statusCode(StatusCode.CREATED)
                .header("Location", "1")
                .body(body)
                .build();

        // then
        assertThat(httpResponse.getStatusLine().getHttpVersion()).isEqualTo(HttpVersion.HTTP_1_1);
        assertThat(httpResponse.getStatusLine().getStatusCode()).isEqualTo(StatusCode.CREATED);
        assertThat(httpResponse.getHeaders().getHeaders()).containsKey("Location");
        assertThat(httpResponse.getBody()).isNotNull();
    }

    @Test
    @DisplayName("HttpResponse에서 statusLine 출력하기")
    void statusLine() {
        // given
        HttpResponse httpResponse = new HttpResponse.Builder()
                .statusCode(StatusCode.CREATED)
                .header("Location", "1")
                .build();

        // when
        String statusLine = httpResponse.statusLine();

        // then
        assertThat(statusLine).isEqualTo("HTTP/1.1 201 Created ");
    }
}
