package nextstep.jwp.controller;

import org.apache.http.*;
import org.junit.jupiter.api.Test;

import static org.apache.http.HttpMethod.GET;
import static org.assertj.core.api.Assertions.assertThat;

class GreetingControllerTest {

    private final Controller controller = new GreetingController();

    @Test
    void 성공코드를_반환한다() throws Exception {
        // given
        final RequestEntity requestEntity = new RequestEntity(GET, "/", null);
        final Headers headers = new Headers();
        headers.put(HttpHeader.CONTENT_LENGTH, "12");
        headers.put(HttpHeader.CONTENT_TYPE, HttpMime.TEXT_HTML.getValue());
        final ResponseEntity expected = new ResponseEntity(headers);

        // when
        final ResponseEntity actual = controller.execute(requestEntity);

        // then
        assertThat(actual).usingRecursiveComparison()
                .ignoringFields("content")
                .isEqualTo(expected);
    }
}
