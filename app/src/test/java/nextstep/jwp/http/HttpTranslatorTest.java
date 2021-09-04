package nextstep.jwp.http;

import nextstep.jwp.http.common.HttpStatusCode;
import nextstep.jwp.http.common.HttpVersion;
import nextstep.jwp.http.message.HeaderFields;
import nextstep.jwp.http.message.MessageBody;
import nextstep.jwp.http.message.MessageHeader;
import nextstep.jwp.http.message.StartLine;
import nextstep.jwp.http.message.request.HttpRequestMessage;
import nextstep.jwp.http.message.request.RequestHeader;
import nextstep.jwp.http.message.request.RequestLine;
import nextstep.jwp.http.message.response.HttpResponseMessage;
import nextstep.jwp.http.message.response.ResponseHeader;
import nextstep.jwp.http.message.response.StatusLine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedHashMap;

import static org.assertj.core.api.Assertions.assertThat;

class HttpTranslatorTest {

    @DisplayName("InputStream 을 번역하여 HttpRequestMessage 를 만든다.")
    @Test
    void translate() throws IOException {
        // given
        InputStream inputStream = generateInputStream();
        OutputStream outputStream = generateOutputStream();
        HttpTranslator httpTranslator = new HttpTranslator(inputStream, outputStream);

        // when
        HttpRequestMessage httpRequestMessage = httpTranslator.translate();

        // then
        StartLine requestLine = httpRequestMessage.getStartLine();
        MessageHeader header = httpRequestMessage.getHeader();
        MessageBody body = httpRequestMessage.getBody();

        assertThat(requestLine).isEqualTo(
                RequestLine.from(requestLineMessage())
        );
        assertThat(header).isEqualTo(
                new RequestHeader(requestHeaderMessage())
        );
        assertThat(body).isEqualTo(
                new MessageBody(requestBodyMessage())
        );
    }

    @DisplayName("HttpResponseMessage 를 응답한다.")
    @Test
    void respond() throws IOException {
        // given
        String expected = String.join("\r\n",
                "HTTP/1.1 200 OK",
                "Content-Type: text/html;charset=utf-8",
                "Content-Length: 12",
                "",
                "hello world!");

        InputStream inputStream = generateInputStream();
        OutputStream outputStream = generateOutputStream();
        HttpTranslator httpTranslator = new HttpTranslator(inputStream, outputStream);

        // when
        httpTranslator.respond(httpResponseMessage());

        // then
        assertThat(outputStream.toString()).isEqualTo(expected);
    }

    private InputStream generateInputStream() {
        String requestMessage = String.join("\r\n",
                requestHeaderMessage(),
                "",
                requestBodyMessage());

        return new ByteArrayInputStream(requestMessage.getBytes());
    }

    private String requestLineMessage() {
        return "POST /index.html HTTP/1.1 \r\n";
    }

    private String requestHeaderMessage() {
        return String.join("\r\n",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Content-Length: 12");
    }

    private String requestBodyMessage() {
        return "hello world!";
    }

    private OutputStream generateOutputStream() {
        return new ByteArrayOutputStream();
    }

    private HttpResponseMessage httpResponseMessage() {
        StatusLine statusLine = new StatusLine(HttpVersion.HTTP_1_1, HttpStatusCode.OK);

        LinkedHashMap<String, String> fields = new LinkedHashMap<>();
        fields.put("Content-Type", "text/html;charset=utf-8");
        fields.put("Content-Length", "12");
        HeaderFields headerFields = new HeaderFields(fields);
        ResponseHeader responseHeader = new ResponseHeader(headerFields);

        MessageBody responseBody = new MessageBody("hello world!");
        return new HttpResponseMessage(statusLine, responseHeader, responseBody);
    }
}
