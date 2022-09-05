package nextstep.jwp.controller;

import org.apache.http.*;
import org.junit.jupiter.api.Test;

import static org.apache.http.HttpMethod.*;
import static org.assertj.core.api.Assertions.assertThat;

class LoginControllerTest {

    private final Controller controller = new LoginController();

    @Test
    void account값과_password값이_일치하면_OK를_반환한다() throws Exception {
        // given
        final RequestEntity requestEntity = new RequestEntity(GET, "/login", "account=gugu&password=password");
        final ResponseEntity expected = new ResponseEntity().contentType(HttpMime.TEXT_HTML);
        // when
        final ResponseEntity actual = controller.execute(requestEntity);

        // then
        assertThat(actual).usingRecursiveComparison()
                .ignoringFields("contentLength", "content")
                .isEqualTo(expected);
    }

    @Test
    void account값이_일치하지_않으면_BAD_REQUEST를_반환한다() throws Exception {
        // given
        final RequestEntity requestEntity = new RequestEntity(GET, "/login", "account=gonggong&password=password");
        final ResponseEntity expected = new ResponseEntity().httpStatus(HttpStatus.BAD_REQUEST)
                .contentType(HttpMime.TEXT_HTML);

        // when
        final ResponseEntity actual = controller.execute(requestEntity);

        // then
        assertThat(actual).usingRecursiveComparison()
                .ignoringFields("contentLength", "content")
                .isEqualTo(expected);
    }

    @Test
    void paasword값이_일치하지_않으면_BAD_REQUEST를_반환한다() {
        // given
        final RequestEntity requestEntity = new RequestEntity(GET, "/login", "account=gugu&password=password1");
        final ResponseEntity expected = new ResponseEntity().httpStatus(HttpStatus.BAD_REQUEST)
                .contentType(HttpMime.TEXT_HTML);

        // when
        final ResponseEntity actual = controller.execute(requestEntity);

        // then
        assertThat(actual).usingRecursiveComparison()
                .ignoringFields("contentLength", "content")
                .isEqualTo(expected);
    }
}
