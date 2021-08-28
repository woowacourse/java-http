package nextstep.jwp.http.message.response.response_line;

import static org.assertj.core.api.Assertions.assertThat;

import nextstep.jwp.http.message.element.HttpStatus;
import nextstep.jwp.http.message.element.HttpVersion;
import org.junit.jupiter.api.Test;

class ResponseLineTest {

    @Test
    void asString() {
        ResponseLine responseLine = new ResponseLine(HttpVersion.HTTP1_1, HttpStatus.OK);

        assertThat(responseLine.asString()).isEqualTo("HTTP/1.1 200 OK");
    }
}
