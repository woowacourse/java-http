package nextstep.jwp.controller;

import nextstep.jwp.exception.HttpRequestException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.*;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ResourceControllerTest {

    @Test
    void POST_요청이_들어오면_예외를_반환한다() throws IOException {
        // given
        final var resourceController = new ResourceController();
        final var requestLine = "POST /index.html HTTP/1.1";
        final var requestHeader = String.join("\r\n",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Content-Length: 58",
                "Content-Type: application/x-www-form-urlencoded",
                "Accept: */*");
        final var requestBody = "account=mango&password=password&email=mango%40woowahan.com";
        final var request = String.join("\r\n", requestLine, requestHeader, "", requestBody);
        final var inputStream = new ByteArrayInputStream(request.getBytes());
        final var inputStreamReader = new InputStreamReader(inputStream);
        final var bufferedReader = new BufferedReader(inputStreamReader);
        final var httpRequest = HttpRequest.from(bufferedReader);
        final var httpResponse = HttpResponse.createEmpty();

        // when & then
        assertThatThrownBy(() -> resourceController.doPost(httpRequest, httpResponse))
                .isInstanceOf(HttpRequestException.class);
        bufferedReader.close();
        inputStreamReader.close();
        inputStream.close();
    }

    @Test
    void GET_요청이_들어오면_HttpResponse에_응답을_처리한다() throws IOException {
        // given
        final var resourceController = new ResourceController();
        final var requestLine = "GET /index.html HTTP/1.1";
        final var requestHeader = String.join("\r\n",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Accept: */*",
                "");
        final var request = String.join("\r\n", requestLine, requestHeader, "");
        final var inputStream = new ByteArrayInputStream(request.getBytes());
        final var inputStreamReader = new InputStreamReader(inputStream);
        final var bufferedReader = new BufferedReader(inputStreamReader);
        final var httpRequest = HttpRequest.from(bufferedReader);
        final var httpResponse = HttpResponse.createEmpty();

        final var expectedStatusLine = StatusLine.of("HTTP/1.1", HttpStatus.OK);
        final var expectedResponseHeader = ResponseHeader.createEmpty();
        final var expectedResponseBody = ResponseBody.fromUri("/index.html");
        expectedResponseHeader.addHeader("Content-Type", "text/html;charset=utf-8");
        expectedResponseHeader.addHeader("Content-Length", String.valueOf(expectedResponseBody.getBody().getBytes().length));

        // when
        resourceController.doGet(httpRequest, httpResponse);

        // then
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(httpResponse.getStatusLine())
                    .usingRecursiveComparison()
                    .isEqualTo(expectedStatusLine);
            softAssertions.assertThat(httpResponse.getResponseHeader())
                    .usingRecursiveComparison()
                    .isEqualTo(expectedResponseHeader);
            softAssertions.assertThat(httpResponse.getResponseBody())
                    .usingRecursiveComparison()
                    .isEqualTo(expectedResponseBody);
        });
        bufferedReader.close();
        inputStreamReader.close();
        inputStream.close();
    }
}
