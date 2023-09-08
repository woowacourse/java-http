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
import org.junit.jupiter.api.Test;
import support.StubSocket;

import java.io.IOException;
import java.util.EnumMap;

import static org.assertj.core.api.Assertions.assertThat;

class Http11ProcessorTest {

    @Test
    void process() {
        // given
        final var socket = new StubSocket();
        final var processor = new Http11Processor(socket, new FrontHandler(new SessionManager()));

        // when
        processor.process(socket);

        // then
        final HttpResponse expected = new HttpResponse();
        expected.changeStatusLine(StatusLine.from(StatusCode.OK));
        expected.addHeader(HttpHeader.CONTENT_TYPE, ContentType.HTML.getValue());
        expected.changeBody(new HttpBody("Hello world!"));

        assertThat(socket.output()).isEqualTo(expected.serialize());
    }

    @Test
    void index_html을_GET_요청하면_해당_페이지로_이동한다() throws IOException {
        // given
        final RequestLine requestLine = new RequestLine(HttpMethod.GET, new RequestUri("/index.html"), Protocol.HTTP_1_1);
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
        expected.changeBody(HttpBody.file("/index.html"));

        assertThat(socket.output()).isEqualTo(expected.serialize());
    }
}
