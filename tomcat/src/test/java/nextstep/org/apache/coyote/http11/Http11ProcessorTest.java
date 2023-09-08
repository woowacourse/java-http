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
import org.apache.coyote.http11.Http11Processor;
import org.junit.jupiter.api.Test;
import support.StubSocket;

import java.io.IOException;
import java.util.EnumMap;

import static org.assertj.core.api.Assertions.assertThat;

class Http11ProcessorTest {

    private static final String SPACE = " ";
    private static final String HEADER_DELIMETER = ": ";
    private static final String CRLF = "\r\n";
    private static final String BLANK_LINE = "";

    @Test
    void process() {
        // given
        final var socket = new StubSocket();
        final var processor = new Http11Processor(socket, new FrontHandler());

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
    void index() throws IOException {
        // given
        final RequestLine requestLine = new RequestLine(HttpMethod.GET, new RequestUri("/index.html"), Protocol.HTTP_1_1);
        final HttpHeaders headers = new HttpHeaders(new EnumMap<>(HttpHeader.class));
        headers.add(HttpHeader.HOST, "localhost:8080");
        headers.add(HttpHeader.CONNECTION, "keep-alive");
        final HttpRequest httpRequest = new HttpRequest(requestLine, headers, HttpBody.empty());

        final var socket = new StubSocket(serialize(httpRequest));
        final Http11Processor processor = new Http11Processor(socket, new FrontHandler());

        // when
        processor.process(socket);

        // then
        final HttpResponse expected = new HttpResponse();
        expected.changeStatusLine(StatusLine.from(StatusCode.OK));
        expected.addHeader(HttpHeader.CONTENT_TYPE, ContentType.HTML.getValue());
        expected.changeBody(HttpBody.file("/index.html"));

        assertThat(socket.output()).isEqualTo(expected.serialize());
    }

    private String serialize(final HttpRequest request) {
        final StringBuilder builder = new StringBuilder();

        serializeRequestLine(request, builder);
        serializeHeaders(request, builder);

        if (request.getHttpBody() == null || request.getHttpBody().getValue().isEmpty()) {
            builder.append(CRLF);
            return builder.toString();
        }

        builder.append(HttpHeader.CONTENT_LENGTH.getValue())
                .append(HEADER_DELIMETER)
                .append(request.getHttpBody().getValue().getBytes().length)
                .append(SPACE).append(CRLF);

        serializeBody(request, builder);
        return builder.toString();
    }

    private void serializeRequestLine(final HttpRequest request, final StringBuilder builder) {
        builder.append(request.getRequestLine().getHttpMethod().name()).append(SPACE);
        builder.append(request.getRequestLine().getRequestUri().getRequestUri()).append(SPACE);
        builder.append(request.getRequestLine().getProtocol().getName()).append(SPACE).append(CRLF);
    }

    private void serializeHeaders(final HttpRequest request, final StringBuilder builder) {
        request.getHeaders().getHeaders()
                .forEach((key, value) -> builder.append(key.getValue()).append(HEADER_DELIMETER).append(value).append(SPACE).append(CRLF));
    }

    private void serializeBody(final HttpRequest request, final StringBuilder builder) {
        builder.append(BLANK_LINE).append(CRLF);
        builder.append(request.getHttpBody().getValue());
    }
}
