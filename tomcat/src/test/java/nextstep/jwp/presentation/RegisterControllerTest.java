package nextstep.jwp.presentation;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http11.file.ResourceLoader;
import org.apache.coyote.http11.support.HttpHeaders;
import org.apache.coyote.http11.support.HttpStatus;
import org.apache.coyote.http11.web.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.util.LinkedHashMap;

class RegisterControllerTest {

    @DisplayName("회원가입 페이지를 로드한다.")
    @Test
    void render() throws IOException {
        // given
        final HttpResponse expected = new HttpResponse(
                HttpStatus.OK,
                new HttpHeaders(new LinkedHashMap<>()),
                ResourceLoader.getContent("register.html")
        );

        // when
        final HttpResponse actual = new RegisterController().render();

        // then
        assertThat(actual).isEqualTo(expected);
    }
}
