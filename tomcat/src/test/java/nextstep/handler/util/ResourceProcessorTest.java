package nextstep.handler.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import nextstep.handler.util.exception.ResourceNotFoundException;
import org.apache.coyote.http.request.HttpRequestBody;
import org.apache.coyote.http.request.HttpRequestHeaders;
import org.apache.coyote.http.request.Parameters;
import org.apache.coyote.http.request.Request;
import org.apache.coyote.http.request.RequestLine;
import org.apache.coyote.http.response.ContentType;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ResourceProcessorTest {

    @Test
    void readResourceFile_메서드는_전달_리소스_파일의_내용을_반환한다() throws IOException {
        final String existsFile = "nextstep.txt";

        final String actual = ResourceProcessor.readResourceFile(existsFile);

        assertThat(actual).isEqualTo("nextstep");
    }

    @Test
    void readResourceFile_메서드는_존재하지_않는_리소스_파일을_전달하면_예외가_발생한다() {
        final String notExistsFile = "asdfadsf";

        assertThatThrownBy(() -> ResourceProcessor.readResourceFile(notExistsFile))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("지정한 리소스가 존재하지 않습니다.");
    }

    @Test
    void findContentType_메서드는_해당_리소스의_Content_Type을_반환한다() {
        final HttpRequestHeaders headers = HttpRequestHeaders.from("Content-Type: text/html;charset=utf-8");
        final RequestLine requestLine = RequestLine.from("GET /index.html HTTP/1.1");
        final Request request = new Request(headers, requestLine, HttpRequestBody.EMPTY, Parameters.EMPTY);
        final String existsFile = "static/index.html";

        final ContentType actual = ResourceProcessor.findContentType(request, existsFile);

        assertThat(actual).isEqualTo(ContentType.TEXT_HTML);
    }
}
