package nextstep.jwp;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.ResourceLocator;
import org.apache.coyote.http11.request.spec.HttpHeaders;
import org.apache.coyote.http11.request.spec.StartLine;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.spec.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LoginControllerTest {

    @Test
    @DisplayName("Content-Type이 application/x-www-form-urlencoded이 아닌 경우 415 응답을 반환한다")
    void unsupportedMediaType() {
        // given
        LoginController loginController = new LoginController(new ResourceLocator("/static"));
        HttpRequest httpRequest = new HttpRequest(
                StartLine.from("POST /login HTTP/1.1\r\n"),
                HttpHeaders.from(List.of("Content-Type: text/html\r\n")),
                null
        );
        HttpResponse httpResponse = new HttpResponse(HttpStatus.OK);

        loginController.doPost(httpRequest, httpResponse);

        assertThat(httpResponse.getStatus()).isEqualTo(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }
}
