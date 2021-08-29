package nextstep.jwp.http.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import nextstep.jwp.http.common.HttpStatus;
import nextstep.jwp.model.StaticResource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class HttpResponseTest {

    private HttpResponse httpResponse;

    @DisplayName("Body를 포함한 HttpResponse는")
    @Nested
    class HttpResponseWithBody {

        @DisplayName("toBytes 요청시 body 관련 정보를 반환한다.")
        @Test
        void toBytes() throws IOException {
            // given
            URL url = ClassLoader.getSystemResource("testfile.html");
            File file = new File(url.getFile());
            StaticResource staticResource = StaticResource.from(file);

            String expectString = "\n\n"
                + "HTTP/1.1 200 OK \n"
                + "Content-Length: 12 \n"
                + "Content-Type: text/html; charset=UTF-8 \n"
                + "\n"
                + "hello world!";

            // when
            httpResponse = HttpResponse.withBody(HttpStatus.OK, staticResource);

            // then
            assertThat(httpResponse.toBytes()).isEqualTo(expectString.getBytes(StandardCharsets.UTF_8));
        }
    }

    @DisplayName("Redirect HttpResponse는")
    @Nested
    class HttpResponseOfRedirect {

        @DisplayName("toBytes 요청시 Redirect Location 정보를 반환한다.")
        @Test
        void toBytes() {
            // given
            String location = "/index.html";

            String expectString = "\n\n"
                + "HTTP/1.1 301 Moved Permanently \n"
                + "Location: /index.html ";

            // when
            httpResponse = HttpResponse.redirect(HttpStatus.MOVED_PERMANENTLY, location);

            // then
            assertThat(httpResponse.toBytes()).isEqualTo(expectString.getBytes(StandardCharsets.UTF_8));
        }
    }
}