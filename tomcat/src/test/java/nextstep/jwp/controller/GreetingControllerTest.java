package nextstep.jwp.controller;

import org.apache.http.HttpStatus;
import org.apache.http.RequestEntity;
import org.apache.http.ResponseEntity;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GreetingControllerTest {

    private final Controller controller = new GreetingController();

    @Test
    void 성공코드를_반환한다() throws Exception {
        // given
        final RequestEntity requestEntity = new RequestEntity("text/html", "/", null);

        // when
        final ResponseEntity actual = controller.execute(requestEntity);

        // then
        assertThat(actual.getHttpStatus()).isEqualTo(HttpStatus.OK);
    }
}
