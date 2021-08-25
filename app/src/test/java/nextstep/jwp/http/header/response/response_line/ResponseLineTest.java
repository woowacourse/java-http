package nextstep.jwp.http.header.response.response_line;

import static org.assertj.core.api.Assertions.assertThat;

import nextstep.jwp.http.header.element.HttpStatus;
import nextstep.jwp.http.header.element.HttpVersion;
import org.junit.jupiter.api.Test;

class ResponseLineTest {

    @Test
    void asString() {
        ResponseLine responseLine = new ResponseLine(HttpVersion.HTTP1_1, HttpStatus.OK);

        assertThat(responseLine.asString()).isEqualTo("HTTP/1.1 200 OK");
    }
}
