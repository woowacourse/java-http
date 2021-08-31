package nextstep.jwp.infrastructure.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.infrastructure.http.handler.ControllerHandler;
import nextstep.jwp.infrastructure.http.handler.FileHandler;
import nextstep.jwp.infrastructure.http.request.HttpRequest;
import nextstep.jwp.infrastructure.http.request.Method;
import nextstep.jwp.infrastructure.http.request.RequestLine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HandlerMappingTest {

    private HandlerMapping handlerMapping;

    @BeforeEach
    void setUp() {
        handlerMapping = new HandlerMapping("nextstep.jwp.controller");
    }

    @DisplayName("경로에 맞는 컨트롤러가 없다면 파일 핸들러를 반환")
    @Test
    void getHandler() {
        final HttpRequest request = new HttpRequest(new RequestLine(Method.GET, "/asdfdasfasdf"), new Headers(), "");

        assertThat(handlerMapping.getHandler(request)).isExactlyInstanceOf(FileHandler.class);
    }


}