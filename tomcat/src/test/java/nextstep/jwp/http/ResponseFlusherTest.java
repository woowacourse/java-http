package nextstep.jwp.http;

import org.apache.http.HttpHeader;
import org.apache.http.HttpStatus;
import org.apache.http.Response;
import org.junit.jupiter.api.Test;

import java.io.OutputStream;

import static org.assertj.core.api.Assertions.assertThat;

class ResponseFlusherTest {

    @Test
    void Response에_해당하는_내용이_OutputStream에_write된다() {
        // given
        final OutputStream outputStream = new MockOutputStream();
        final Response response = new Response(outputStream).header(HttpHeader.LOCATION, "/index.html")
                .httpStatus(HttpStatus.FOUND);

        final String expected = response.parse();

        // when
        ResponseFlusher.flush(response);

        // then
        assertThat(outputStream).hasToString(expected);
    }
}
