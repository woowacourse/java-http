package nextstep.jwp.http.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import nextstep.jwp.model.ContentType;
import nextstep.jwp.model.StaticResource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ResponseHeadersTest {

    private static final String NEW_LINE = System.getProperty("line.separator");

    private ResponseHeaders responseHeaders;

    @DisplayName("Body ResponseHeaders는")
    @Nested
    class ResponseHeaderOfBody {

        @DisplayName("toString 요청시 Content-Type과 Content-Length 문자열을 반환한다.")
        @Test
        void toStringTest() throws IOException {
            // given
            ContentType contentType = ContentType.findByExtension("html");
            URL url = ClassLoader.getSystemResource("static/index.html");
            File file = new File(url.getFile());
            byte[] bytes = Files.readAllBytes(file.toPath());
            StaticResource staticResource = StaticResource.from(file);

            String expectContentTypeString = String.format("Content-Type: %s ", contentType.getType());
            String expectContentLengthString = String.format("Content-Length: %d ", bytes.length);

            // when
            responseHeaders = ResponseHeaders.ofBody(staticResource);
            String headersString = responseHeaders.toString();
            String[] headersStrings = headersString.split(NEW_LINE);

            // then
            assertThat(headersStrings).containsExactlyInAnyOrder(expectContentTypeString, expectContentLengthString);
        }
    }

    @DisplayName("Redirect location ResponseHeaders는")
    @Nested
    class ResponseHeaderOfRedirect {

        @DisplayName("toString 요청시 Location 문자열을 반환한다.")
        @Test
        void toStringTest() {
            // given
            String location = "/index.html";

            String expectHeadersString = String.format("Location: %s ", location);

            // when
            responseHeaders = ResponseHeaders.ofRedirect(location);
            String headersString = responseHeaders.toString();

            // then
            assertThat(headersString).isEqualTo(expectHeadersString);
        }
    }
}