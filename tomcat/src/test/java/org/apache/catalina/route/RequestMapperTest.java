package org.apache.catalina.route;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.catalina.controller.AbstractController;
import org.apache.catalina.controller.StaticResourceController;
import org.apache.coyote.http11.common.HttpHeader;
import org.apache.coyote.http11.common.HttpMethod;
import org.apache.coyote.http11.common.HttpStatusCode;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.request.RequestLine;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestMapperTest {

    @DisplayName("새로운 컨트롤러를 등록한다.")
    @Test
    void registerController() {
        // given
        RequestMapper requestMapper = new RequestMapper();

        // when
        requestMapper.register(getAnonymousController(
                HttpMethod.GET, "/test", HttpStatusCode.OK, "test", false
        ));

        // then
        HttpRequest request = generateRequestOnlyContainsRequestLine("GET /test HTTP/1.1");
        assertThat(requestMapper.getController(request)).isPresent();
    }

    @DisplayName("매칭되는 컨트롤러가 없는 경우 Optional.empty()를 반환한다.")
    @Test
    void getControllerWhenControllerIsNotRegistered() {
        // given
        RequestMapper requestMapper = new RequestMapper();

        // when
        HttpRequest request = generateRequestOnlyContainsRequestLine("GET /test HTTP/1.1");

        // then
        assertThat(requestMapper.getController(request)).isEmpty();
    }

    @DisplayName("경로에 매칭되는 파일이 존재하는 경우 정적 리소스 컨트롤러를 반환한다.")
    @Test
    void getControllerWhenStaticResource() {
        // given
        RequestMapper requestMapper = new RequestMapper();

        // when
        HttpRequest request = generateRequestOnlyContainsRequestLine("GET /index.html HTTP/1.1");

        // then
        assertThat(requestMapper.getController(request)).containsInstanceOf(StaticResourceController.class);
    }

    @DisplayName("등록한 컨트롤러와 정적 리소스 경로 모두에 매칭되는 경우 컨트롤러가 우선순위를 가진다.")
    @Test
    void getControllerWhenBothControllerAndStaticResource() {
        // given
        RequestMapper requestMapper = new RequestMapper();
        requestMapper.register(getAnonymousController(
                HttpMethod.GET, "/index.html", HttpStatusCode.OK, "test", false
        ));

        // when
        HttpRequest request = generateRequestOnlyContainsRequestLine("GET /index.html HTTP/1.1");

        // then
        assertThat(requestMapper.getController(request)).isNotInstanceOf(StaticResourceController.class);
    }

    private HttpRequest generateRequestOnlyContainsRequestLine(String requestLine) {
        return new HttpRequest(
                RequestLine.from(requestLine), HttpHeader.from(""), RequestBody.empty()
        );
    }

    private AbstractController getAnonymousController(
            HttpMethod mapMethod, String mapPath, HttpStatusCode responseStatus, String responseBody, boolean exception
    ) {
        return new AbstractController() {
            @Override
            protected void doGet(HttpRequest request, HttpResponse response) {
                process(request, response);
            }

            @Override
            protected void doPost(HttpRequest request, HttpResponse response) {
                process(request, response);
            }

            private void process(HttpRequest request, HttpResponse response) {
                if (exception) {
                    throw new RuntimeException(String.format("%s %s 의도적 예외 발생", mapMethod, mapPath));
                }
                if (request.getMethod().equals(mapMethod)) {
                    response.setStatus(responseStatus)
                            .setBody(responseBody);
                }
            }

            @Override
            public String matchedPath() {
                return mapPath;
            }
        };
    }
}
