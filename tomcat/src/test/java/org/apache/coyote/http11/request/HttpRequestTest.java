package org.apache.coyote.http11.request;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HttpRequestTest {

    @Test
    void BufferedReader_통한_입력_값으로_HttpRequest_객체를_생성한다() throws IOException {
        // given
        final var requestLine = "POST /register HTTP/1.1";
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

        final var expectedRequestLine = RequestLine.from(requestLine);
        final var splitRequestHeader = Arrays.stream(requestHeader.split("\r\n"))
                .collect(Collectors.toList());
        final var expectedRequestHeader = RequestHeader.from(splitRequestHeader);

        // when
        final var actual = HttpRequest.from(bufferedReader);

        // then
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(actual.getRequestLine())
                    .usingRecursiveComparison()
                    .isEqualTo(expectedRequestLine);
            softAssertions.assertThat(actual.getRequestHeader())
                    .usingRecursiveComparison()
                    .isEqualTo(expectedRequestHeader);
            softAssertions.assertThat(actual.getRequestBody())
                    .isEqualTo(requestBody);
        });
        bufferedReader.close();
        inputStreamReader.close();
        inputStream.close();
    }
}
