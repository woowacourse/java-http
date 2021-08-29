package nextstep.jwp.model;

import nextstep.jwp.model.httpmessage.response.ResponseLine;
import org.junit.jupiter.api.Test;

import static nextstep.jwp.model.httpmessage.response.HttpStatus.OK;
import static nextstep.jwp.model.httpmessage.response.HttpStatus.REDIRECT;
import static org.assertj.core.api.Assertions.assertThat;

class ResponseLineTest {

    @Test
    void ok() {
        ResponseLine responseLine = new ResponseLine(OK);
        assertThat(responseLine.toString()).hasToString("HTTP/1.1 200 OK ");
    }

    @Test
    void redirect() {
        ResponseLine responseLine = new ResponseLine(REDIRECT);
        assertThat(responseLine.toString()).hasToString("HTTP/1.1 302 Redirect ");
    }
}