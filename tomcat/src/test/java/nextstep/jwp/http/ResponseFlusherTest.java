package nextstep.jwp.http;

import org.apache.coyote.HttpHeader;
import org.apache.coyote.HttpStatus;
import org.apache.coyote.support.Response;
import org.apache.coyote.support.ResponseFlusher;
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
