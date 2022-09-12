package nextstep.jwp.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.coyote.http11.handler.HandlerResponseEntity;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestHeader;
import org.apache.coyote.http11.request.HttpRequestLine;
import org.apache.coyote.http11.response.HttpResponseHeader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HomeControllerTest {

    private final HomeController homeController = HomeController.getInstance();

    private HttpRequest getHttpRequest() {
        final HttpRequestLine requestLine = HttpRequestLine.from("GET / HTTP/1.1");
        final List<String> rawRequest = new ArrayList<>();
        rawRequest.add("name: eve");
        final HttpRequestHeader httpRequestHeader = HttpRequestHeader.from(rawRequest);

        return HttpRequest.of(requestLine, httpRequestHeader, "");
    }

    @Test
    @DisplayName("doGet 메소드는 body에 text/html 값을 담은 HandlerResponseEntity를 반환한다.")
    void doGet() {
        // given

        // when
        final HandlerResponseEntity response = homeController.doGet(getHttpRequest(),
                new HttpResponseHeader(new HashMap<>()));

        // then
        assertThat(response.getResource()).isEqualTo("Hello world!");
    }

    @Test
    @DisplayName("doPost 메소드는 IllegalStateException 예외를 던진다.")
    void doPost() {
        // when & then
        assertThatThrownBy(() -> homeController.doPost(
                getHttpRequest(), new HttpResponseHeader(new HashMap<>())))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Invalid Uri");
    }
}