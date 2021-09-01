package nextstep.jwp;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.IndexController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.NotFoundController;
import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.RequestBody;
import nextstep.jwp.http.request.RequestHeader;
import nextstep.jwp.http.request.RequestLine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class ControllerTest {

    @Nested
    @DisplayName("호출 컨트롤러 확인 테스트")
    public class CallControllerTest {

        @Test
        @DisplayName("컨트롤러의 값을 찾지 못할 경우 NotFoundController 호출")
        void notFound() {
            //given
            RequestLine requestLine = RequestLine.createFromPlainText("GET /index2.html HTTP/1.1 ");
            RequestHeader requestHeader = new RequestHeader(new HashMap<>() {{
                put("Host", "localhost:8080 ");
                put("Connection", "keep-alive ");
            }});
            RequestBody requestBody = new RequestBody("");
            HttpRequest httpRequest = new HttpRequest(requestLine, requestHeader, requestBody);

            RequestMapper requestMapper = new RequestMapper();

            // when
            Controller controller = requestMapper.getController(httpRequest);

            // then
            assertThat(controller).isInstanceOf(NotFoundController.class);
        }

        @Test
        @DisplayName("index 컨트롤러 호출")
        void index() {
            //given
            RequestLine requestLine = RequestLine.createFromPlainText("GET /index.html HTTP/1.1 ");
            RequestHeader requestHeader = new RequestHeader(new HashMap<>() {{
                put("Host", "localhost:8080 ");
                put("Connection", "keep-alive ");
            }});
            RequestBody requestBody = new RequestBody("");
            HttpRequest httpRequest = new HttpRequest(requestLine, requestHeader, requestBody);

            RequestMapper requestMapper = new RequestMapper();

            // when
            Controller controller = requestMapper.getController(httpRequest);

            // then
            assertThat(controller).isInstanceOf(IndexController.class);
        }

        @Test
        @DisplayName("register 컨트롤러 호출")
        void register() {
            //given
            RequestLine requestLine = RequestLine.createFromPlainText("GET /register HTTP/1.1 ");
            RequestHeader requestHeader = new RequestHeader(new HashMap<>() {{
                put("Host", "localhost:8080 ");
                put("Connection", "keep-alive ");
            }});
            RequestBody requestBody = new RequestBody("");
            HttpRequest httpRequest = new HttpRequest(requestLine, requestHeader, requestBody);

            RequestMapper requestMapper = new RequestMapper();

            // when
            Controller controller = requestMapper.getController(httpRequest);

            // then
            assertThat(controller).isInstanceOf(RegisterController.class);
        }

        @Test
        @DisplayName("login 컨트롤러 호출")
        void login() {
            //given
            RequestLine requestLine = RequestLine.createFromPlainText("GET /login HTTP/1.1 ");
            RequestHeader requestHeader = new RequestHeader(new HashMap<>() {{
                put("Host", "localhost:8080 ");
                put("Connection", "keep-alive ");
            }});
            RequestBody requestBody = new RequestBody("");
            HttpRequest httpRequest = new HttpRequest(requestLine, requestHeader, requestBody);

            RequestMapper requestMapper = new RequestMapper();

            // when
            Controller controller = requestMapper.getController(httpRequest);

            // then
            assertThat(controller).isInstanceOf(LoginController.class);
        }
    }
}
