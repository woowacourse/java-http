package org.apache.catalina.route;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http11.common.HttpMethod;
import org.apache.coyote.http11.common.HttpStatusCode;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestMessageReader;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DefaultDispatcherTest {

    private RequestMapping requestMapping;

    @BeforeEach
    void setUp() {
        requestMapping = new RequestMapper();
    }

    @DisplayName("핸들러를 등록하여 요청을 처리한다.")
    @Test
    void dispatch() throws IOException {
        // given
        requestMapping.register(getAnonymousController(
                HttpMethod.POST, "/dispatch-test", HttpStatusCode.CREATED, "", false
        ));
        DefaultDispatcher dispatcher = new DefaultDispatcher(requestMapping);

        String rawRequest = String.join("\r\n",
                "POST /dispatch-test HTTP/1.1",
                "", ""
        );
        HttpRequest request = createRequest(rawRequest);
        HttpResponse response = HttpResponse.ok();

        // when
        dispatcher.dispatch(request, response);

        // then
        String expected = String.join("\r\n",
                "HTTP/1.1 201 Created",
                "Content-Length: 0",
                "", ""
        );
        assertThat(response.toHttpMessage()).isEqualTo(expected);
    }

    @DisplayName("요청에 매핑되는 핸들러가 없을 경우 404 응답을 반환한다.")
    @Test
    void dispatchNotFound() throws IOException {
        // given
        DefaultDispatcher dispatcher = new DefaultDispatcher(requestMapping);

        String rawRequest = String.join("\r\n",
                "GET /not-found HTTP/1.1",
                "", ""
        );
        HttpRequest request = createRequest(rawRequest);
        HttpResponse response = HttpResponse.ok();

        // when
        dispatcher.dispatch(request, response);

        // then
        String expected = String.join("\r\n",
                "HTTP/1.1 404 Not Found",
                "Content-Length: 0",
                "", ""
        );
        assertThat(response.toHttpMessage()).isEqualTo(expected);
    }

    @DisplayName("핸들러를 찾지 못한 경우 처리할 기본 핸들러를 등록할 수 있다.")
    @Test
    void setNotFoundHandler() throws IOException {
        // given
        DefaultDispatcher dispatcher = new DefaultDispatcher(requestMapping);
        dispatcher.setNotFoundHandler(CustomNotFoundHandler.class);

        // when
        HttpResponse response = HttpResponse.ok();
        dispatcher.dispatch(createRequest("GET /not-found HTTP/1.1"), response);

        // then
        String expected = String.join("\r\n",
                "HTTP/1.1 404 Not Found",
                "Content-Length: 16",
                "",
                "Custom Not Found"
        );
        assertThat(response.toHttpMessage()).isEqualTo(expected);
    }

    @DisplayName("요청 처리 중 예외가 발생하면 500 응답을 반환한다.")
    @Test
    void dispatchInternalServerError() throws IOException {
        // given
        requestMapping.register(getAnonymousController(
                HttpMethod.GET, "/internal-server-error", HttpStatusCode.INTERNAL_SERVER_ERROR,
                "", true
        ));
        DefaultDispatcher dispatcher = new DefaultDispatcher(requestMapping);

        String rawRequest = String.join("\r\n",
                "GET /internal-server-error HTTP/1.1",
                "", ""
        );
        HttpRequest request = createRequest(rawRequest);
        HttpResponse response = HttpResponse.ok();

        // when
        dispatcher.dispatch(request, response);

        // then
        String expected = String.join("\r\n",
                "HTTP/1.1 500 Internal Server Error",
                "Content-Length: 0",
                "", ""
        );
        assertThat(response.toHttpMessage()).isEqualTo(expected);
    }

    @DisplayName("예외 처리 핸들러를 등록할 수 있다.")
    @Test
    void setUnResolvedErrorHandler() throws IOException {
        // given
        requestMapping.register(getAnonymousController(
                HttpMethod.GET, "/internal-server-error", HttpStatusCode.INTERNAL_SERVER_ERROR,
                "", true
        ));
        DefaultDispatcher dispatcher = new DefaultDispatcher(requestMapping);
        dispatcher.setUnresolvedErrorHandler(CustomErrorController.class);

        String rawRequest = String.join("\r\n",
                "GET /internal-server-error HTTP/1.1",
                ""
        );
        HttpRequest request = createRequest(rawRequest);
        HttpResponse response = HttpResponse.ok();

        // when
        dispatcher.dispatch(request, response);

        // then
        String expected = String.join("\r\n",
                "HTTP/1.1 500 Internal Server Error",
                "Content-Length: 20",
                "",
                "Custom Error Handler"
        );
        assertThat(response.toHttpMessage()).isEqualTo(expected);
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

    private HttpRequest createRequest(String rawRequest) throws IOException {
        InputStream inputStream = new ByteArrayInputStream(rawRequest.getBytes());
        return HttpRequestMessageReader.read(inputStream);
    }
}
