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

class LoginControllerTest {

    @Test
    void 올바른_로그인_요청일_경우_index_화면을_응답한다() {
        // given
        StartLine startLine = new StartLine("POST /login HTTP/1.1 ");
        RequestHeaders requestHeaders = RequestHeaders.of(List.of("Content-Length: 10"));
        RequestBody requestBody = RequestBody.of("account=gugu&password=password");
        HttpRequest httpRequest = new HttpRequest(startLine, requestHeaders, requestBody);

        LoginController loginController = new LoginController();
        HttpResponse response = new HttpResponse();

        // when
        loginController.doPost(httpRequest, response);

        // then
        assertThat(response.asString()).contains("index.html", "302");
    }

    @Test
    void 로그인_페이지_요청일_경우_login_화면을_응답한다() throws URISyntaxException, IOException {
        // given
        StartLine startLine = new StartLine("GET /login HTTP/1.1 ");
        RequestHeaders requestHeaders = RequestHeaders.of(List.of("Content-Length: 10"));
        RequestBody requestBody = RequestBody.of("name=eden&nickName=king");
        HttpRequest httpRequest = new HttpRequest(startLine, requestHeaders, requestBody);

        LoginController loginController = new LoginController();
        HttpResponse response = new HttpResponse();

        // when
        loginController.doGet(httpRequest, response);
        URI uri = getClass().getClassLoader().getResource("static/login.html").toURI();
        String file = new String(Files.readAllBytes(Paths.get(uri)));

        // then
        assertThat(response.asString()).contains(file, "200");
    }

    @Test
    void 로그인_시_계정이_없는_요청일_경우_예외를_던진다() {
        // given
        StartLine startLine = new StartLine("POST /login HTTP/1.1 ");
        RequestHeaders requestHeaders = RequestHeaders.of(List.of("Content-Length: 10"));
        RequestBody requestBody = RequestBody.of("account=eden&password=password");
        HttpRequest httpRequest = new HttpRequest(startLine, requestHeaders, requestBody);

        LoginController loginController = new LoginController();
        HttpResponse response = new HttpResponse();

        // when
        loginController.doPost(httpRequest, response);

        // then
assertThat(response.asString()).contains("401.html", "302");
    }

    @Test
    void 로그인_시_비밀번호가_일치하지_않는_요청일_경우_예외를_던진다() {
        // given
        StartLine startLine = new StartLine("POST /login HTTP/1.1 ");
        RequestHeaders requestHeaders = RequestHeaders.of(List.of("Content-Length: 10"));
        RequestBody requestBody = RequestBody.of("account=gugu&password=gugugugu");
        HttpRequest httpRequest = new HttpRequest(startLine, requestHeaders, requestBody);

        LoginController loginController = new LoginController();
        HttpResponse response = new HttpResponse();

        // when
        loginController.doPost(httpRequest, response);

        // then
        assertThat(response.asString()).contains("401.html", "302");
    }

}
