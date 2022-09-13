package nextstep.org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.HttpResponseHeader;
import org.junit.jupiter.api.Test;

public class HttpResponseHeaderTest {

    @Test
    void createHttpResponseHeader() {
        final HttpResponseHeader httpResponseHeader = HttpResponseHeader.fromContentType(ContentType.HTML);
        final String headers = httpResponseHeader.getAll();

        assertThat(headers).contains("Content-Type: text/html");
    }
}
