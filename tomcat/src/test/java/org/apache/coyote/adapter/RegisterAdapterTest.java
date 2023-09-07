package org.apache.coyote.adapter;

import static org.apache.coyote.FixtureFactory.DEFAULT_HEADERS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.FixtureFactory;
import org.apache.coyote.request.Request;
import org.apache.coyote.request.RequestBody;
import org.apache.coyote.response.HttpStatus;
import org.apache.coyote.view.Resource;
import org.apache.coyote.view.ViewResource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RegisterAdapterTest {

    @Test
    @DisplayName("/register로 접속해서 POST 요청을 보내면 회원가입할 수 있다")
    void register_post() throws IOException, URISyntaxException {
        Map<String, String> body = new HashMap<>();
        body.put("account", "kong");
        body.put("password", "password");
        body.put("email", "kong@kong");
        Request request = FixtureFactory.getPostRequest("/register", DEFAULT_HEADERS, new RequestBody(body));
        Path path = Path.of(ViewResource.class.getResource("/static/index.html").toURI());
        String expectedBody = new String(Files.readAllBytes(path));
        HttpStatus expectedStatus = HttpStatus.SUCCESS;

        RegisterAdapter registerAdapter = new RegisterAdapter();
        Resource actual = registerAdapter.adapt(request);

        assertAll(
                () -> assertThat(actual.getHttpStatus()).isEqualTo(expectedStatus),
                () -> assertThat(actual.getValue()).isEqualTo(expectedBody)
        );
    }

    @Test
    @DisplayName("/register로 접속해서 GET 요청을 보내면 회원가입 페이지를 확인할 수 있다")
    void register_get() throws IOException, URISyntaxException {
        Request request = FixtureFactory.getGetRequest("/register", DEFAULT_HEADERS);
        Path path = Path.of(ViewResource.class.getResource("/static/register.html").toURI());
        String expectedBody = new String(Files.readAllBytes(path));
        HttpStatus expectedStatus = HttpStatus.OK;

        RegisterAdapter registerAdapter = new RegisterAdapter();
        Resource actual = registerAdapter.adapt(request);

        assertAll(
                () -> assertThat(actual.getHttpStatus()).isEqualTo(expectedStatus),
                () -> assertThat(actual.getValue()).isEqualTo(expectedBody)
        );
    }
}
