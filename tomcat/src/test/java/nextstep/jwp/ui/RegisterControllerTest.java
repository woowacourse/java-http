package nextstep.jwp.ui;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.request.RequestHeaders;
import org.apache.coyote.http11.request.StartLine;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.ResponseEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class RegisterControllerTest {

    @Test
    void 회원가입_페이지를_요청하면_register_html을_반환한다() throws IOException, URISyntaxException {
        // given
        StartLine startLine = new StartLine("GET /register HTTP/1.1 ");
        RequestHeaders requestHeaders = RequestHeaders.of(List.of("Content-Type: text/html"));
        RequestBody requestBody = RequestBody.of("");
        HttpRequest httpRequest = new HttpRequest(startLine, requestHeaders, requestBody);

        RegisterController registerController = new RegisterController();
        HttpResponse response = new HttpResponse();

        // when
        URI uri = getClass().getClassLoader().getResource("static/register.html").toURI();
        String file = new String(Files.readAllBytes(Paths.get(uri)));
        registerController.doGet(httpRequest, response);

        // then
        assertThat(response.asString()).contains(file);
    }

    @Test
    void 회원가입을_하면_index_html을_반환한다() throws URISyntaxException, IOException {
        // given
        StartLine startLine = new StartLine("POST /register HTTP/1.1 ");
        RequestHeaders requestHeaders = RequestHeaders.of(List.of("Content-Type: text/html"));
        RequestBody requestBody = RequestBody.of("account=eden&email=eden@morak.com&password=king");
        HttpRequest httpRequest = new HttpRequest(startLine, requestHeaders, requestBody);

        RegisterController registerController = new RegisterController();
        HttpResponse response = new HttpResponse();
        // when
        registerController.doPost(httpRequest, response);
        URI uri = getClass().getClassLoader().getResource("static/index.html").toURI();
        String file = new String(Files.readAllBytes(Paths.get(uri)));

        // then
        assertThat(response.asString()).contains("index.html", "302");
    }

}
