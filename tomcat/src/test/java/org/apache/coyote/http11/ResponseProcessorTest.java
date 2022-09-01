package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.AuthenticationException;
import nextstep.jwp.exception.UserNotFoundException;
import org.junit.jupiter.api.Test;

class ResponseProcessorTest {

    @Test
    void getResponse를_하면_http_형식을_응답한다() throws URISyntaxException, IOException {
        // given
        String fileUri = "/nextstep.txt";
        ResponseProcessor responseProcessor = new ResponseProcessor(fileUri);

        // when
        String response = responseProcessor.getResponse();

        // then
        String fileContent = "nextstep";
        String expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + fileContent.getBytes().length + " ",
                "",
                fileContent);
        assertThat(response).isEqualTo(expected);
    }

    @Test
    void query_parameter로_올바른_계정_정보가_들어오면_print한다() throws URISyntaxException, IOException {
        // given
        String uri = "/login?account=gugu&password=password";
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // when
        new ResponseProcessor(uri);

        // then
        String expected = InMemoryUserRepository.findByAccount("gugu").orElseThrow().toString().concat("\n");
        assertThat(outContent.toString()).isEqualTo(expected);
    }

    @Test
    void query_parameter로_들어온_계정_정보가_없을_경우_예외를_반환한다() {
        // given
        String uri = "/login?account=eden&password=password";

        // when & then
        assertThatThrownBy(() -> new ResponseProcessor(uri))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void query_parameter로_들어온_계정_정보가_일치하지_않을_경우_예외를_반환한다() {
        // given
        String uri = "/login?account=gugu&password=gugugugu";

        // when & then
        assertThatThrownBy(() -> new ResponseProcessor(uri))
                .isInstanceOf(AuthenticationException.class);
    }
}
