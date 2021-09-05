package nextstep.joanne.server.http.request;

import nextstep.joanne.MockSocket;
import nextstep.joanne.server.http.Headers;
import nextstep.joanne.server.http.HttpMethod;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static nextstep.Fixture.makeGetRequest;
import static nextstep.Fixture.makePostRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class HttpRequestParserTest {

    private static final HttpRequestParser httpRequestParser
            = new HttpRequestParser();

    private MockSocket connection;
    private InputStream inputStream;
    private BufferedReader bufferedReader;

    @AfterEach
    void tearDown() throws IOException {
        connection.close();
        inputStream.close();
        bufferedReader.close();
    }

    @Test
    @DisplayName("요청을 파싱해서 HttpRequest로 만든다.")
    void parse() throws IOException {
        // given
        String request = makeGetRequest("/index");
        connection = new MockSocket(request);
        inputStream = connection.getInputStream();
        bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        // when
        HttpRequest httpRequest = httpRequestParser.parse(bufferedReader);

        // then
        RequestLine requestLine = httpRequest.requestLine();
        assertAll(
                () -> assertThat(requestLine.httpMethod()).isEqualTo(HttpMethod.GET),
                () -> assertThat(requestLine.uri()).isEqualTo("/index"),
                () -> assertThat(requestLine.version()).isEqualTo("HTTP/1.1"),
                () -> assertThat(requestLine.queryString()).isEmpty()
        );

        Headers requestHeaders = httpRequest.requestHeaders();
        assertAll(
                () -> assertThat(requestHeaders.headers()).containsKeys("Host", "Connection")
        );

        MessageBody messageBody = httpRequest.messageBody();
        assertAll(
                () -> assertThat(messageBody).isNull()
        );
    }

    @Test
    @DisplayName("쿼리스트링이 포함된 요청을 파싱해서 HttpRequest로 만든다.")
    void parseWithQueryString() throws IOException {
        // given
        String request = makeGetRequest("/index?id=joanne&password=hi");
        connection = new MockSocket(request);
        inputStream = connection.getInputStream();
        bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        // when
        HttpRequest httpRequest = httpRequestParser.parse(bufferedReader);

        // then
        RequestLine requestLine = httpRequest.requestLine();
        assertAll(
                () -> assertThat(requestLine.httpMethod()).isEqualTo(HttpMethod.GET),
                () -> assertThat(requestLine.uri()).isEqualTo("/index"),
                () -> assertThat(requestLine.version()).isEqualTo("HTTP/1.1"),
                () -> assertThat(requestLine.queryString()).containsEntry("id", "joanne"),
                () -> assertThat(requestLine.queryString()).containsEntry("password", "hi")
        );

        Headers requestHeaders = httpRequest.requestHeaders();
        assertAll(
                () -> assertThat(requestHeaders.headers()).containsKeys("Host", "Connection")
        );

        MessageBody messageBody = httpRequest.messageBody();
        assertAll(
                () -> assertThat(messageBody).isNull()
        );
    }

    @Test
    @DisplayName("Body가 포함된 요청을 파싱해서 HttpRequest로 만든다.")
    void parseWithBody() throws IOException {
        // given
        String request = makePostRequest("/index", "id=joanne&password=hi");
        connection = new MockSocket(request);
        inputStream = connection.getInputStream();
        bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        // when
        HttpRequest httpRequest = httpRequestParser.parse(bufferedReader);

        // then
        RequestLine requestLine = httpRequest.requestLine();
        assertAll(
                () -> assertThat(requestLine.httpMethod()).isEqualTo(HttpMethod.POST),
                () -> assertThat(requestLine.uri()).isEqualTo("/index"),
                () -> assertThat(requestLine.version()).isEqualTo("HTTP/1.1"),
                () -> assertThat(requestLine.queryString()).isEmpty()
        );

        Headers requestHeaders = httpRequest.requestHeaders();
        assertAll(
                () -> assertThat(requestHeaders.headers()).containsKeys("Host", "Connection", "Content-Length")
        );

        MessageBody messageBody = httpRequest.messageBody();
        assertAll(
                () -> assertThat(messageBody.body()).containsEntry("id", "joanne"),
                () -> assertThat(messageBody.body()).containsEntry("password", "hi")
        );
    }
}
