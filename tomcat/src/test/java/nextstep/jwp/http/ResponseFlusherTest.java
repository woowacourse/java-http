package nextstep.jwp.http;

import org.apache.http.HttpHeader;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;

import java.io.OutputStream;

import static org.assertj.core.api.Assertions.assertThat;

class ResponseFlusherTest {

    @Test
    void Response에_해당하는_내용이_OutputStream에_write된다() {
        // given
        final Response response = new Response().header(HttpHeader.LOCATION, "/index.html")
                .httpStatus(HttpStatus.FOUND);

        final OutputStream outputStream = new MockOutputStream();

        final String expected = response.parse();

        // when
        ResponseFlusher.flush(outputStream, response);

        // then
        assertThat(outputStream).hasToString(expected);
    }
}
