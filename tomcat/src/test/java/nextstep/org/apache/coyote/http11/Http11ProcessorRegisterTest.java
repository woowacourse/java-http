package nextstep.org.apache.coyote.http11;

import nextstep.jwp.controller.FrontHandler;
import org.apache.coyote.http.common.ContentType;
import org.apache.coyote.http.common.HttpBody;
import org.apache.coyote.http.common.HttpHeader;
import org.apache.coyote.http.common.HttpHeaders;
import org.apache.coyote.http.common.Protocol;
import org.apache.coyote.http.request.HttpMethod;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.request.RequestLine;
import org.apache.coyote.http.request.RequestUri;
import org.apache.coyote.http.response.HttpResponse;
import org.apache.coyote.http.response.StatusCode;
import org.apache.coyote.http.response.StatusLine;
import org.apache.coyote.http.session.SessionManager;
import org.apache.coyote.http11.Http11Processor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import support.StubSocket;

import java.io.IOException;
import java.util.EnumMap;

import static org.assertj.core.api.Assertions.assertThat;

class Http11ProcessorRegisterTest {

    @DisplayName("회원가입 관련 테스트")
    @Nested
    class Register {

        @Test
        void register으로_POST_요청하면_해당_페이지로_이동한다() throws IOException {
            // given
            final RequestLine requestLine = new RequestLine(HttpMethod.POST, new RequestUri("/register"), Protocol.HTTP_1_1);
            final HttpHeaders headers = new HttpHeaders(new EnumMap<>(HttpHeader.class));
            headers.add(HttpHeader.HOST, "localhost:8080");
            headers.add(HttpHeader.CONTENT_TYPE, "application/x-www-form-urlencoded");
            final HttpRequest httpRequest = new HttpRequest(requestLine, headers, new HttpBody("account=gugu&email=gugu@gugu.com&password=password"));

            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket, new FrontHandler(new SessionManager()));

            // when
            processor.process(socket);

            // then
            assertThat(socket.output())
                    .contains(StatusCode.FOUND.name())
                    .contains("/index.html");
        }

        @Test
        void register으로_GET_요청하면_register_html로_리다이렉트_된다() throws IOException {
            // given
            final RequestLine requestLine = new RequestLine(HttpMethod.GET, new RequestUri("/login"), Protocol.HTTP_1_1);
            final HttpHeaders headers = new HttpHeaders(new EnumMap<>(HttpHeader.class));
            headers.add(HttpHeader.HOST, "localhost:8080");
            final HttpRequest httpRequest = new HttpRequest(requestLine, headers, HttpBody.empty());

            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket, new FrontHandler(new SessionManager()));

            // when
            processor.process(socket);

            // then
            final HttpResponse expected = new HttpResponse();
            expected.changeStatusLine(StatusLine.from(StatusCode.OK));
            expected.addHeader(HttpHeader.CONTENT_TYPE, ContentType.HTML.getValue());
            expected.changeBody(HttpBody.file("/login.html"));

            assertThat(socket.output()).isEqualTo(expected.serialize());
        }
    }
}
