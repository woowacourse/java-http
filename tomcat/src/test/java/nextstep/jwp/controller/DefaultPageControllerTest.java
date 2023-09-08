package nextstep.jwp.controller;

import static org.apache.coyote.http11.HttpStatus.OK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Map;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpRequestLine;
import org.apache.coyote.http11.HttpResponse;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class DefaultPageControllerTest {

    private final DefaultPageController defaultPageController = new DefaultPageController();

    @Test
    void 기본_페이지로_이동() throws Exception {
        // given
        HttpRequest request = createRequest();
        HttpResponse response = new HttpResponse();

        // when
        defaultPageController.doGet(request, response);

        // then
        assertAll(
            () -> assertThat(response.getBody()).isEqualTo("Hello world!"),
            () -> assertThat(response.getHttpStatus()).isEqualTo(OK)
        );
    }

    private HttpRequest createRequest() {
        return new HttpRequest(
            new HttpRequestLine(HttpMethod.GET, "/", "version"),
            null,
            Map.of(),
            ""
        );
    }
}
