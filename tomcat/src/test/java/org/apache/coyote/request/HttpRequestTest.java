package org.apache.coyote.request;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.List;
import org.apache.coyote.common.ContentType;
import org.apache.coyote.common.HttpMethod;
import org.apache.coyote.common.HttpVersion;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HttpRequestTest {

    @Test
    void HTTP_메시지를_파싱해서_HTTP_요청_객체를_생성한다() throws Exception {
        final String httpRequestMessage = String.join("\r\n",
                "POST /index.html HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Content-Length: 12",
                "Accept: */*;q=0.1, text/html;q=0.8, application/json;q=0.5",
                "",
                "Hello world!"
        );
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(httpRequestMessage.getBytes());
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        final HttpRequest httpRequest = HttpRequest.parse(bufferedReader);

        assertSoftly(softly -> {
            softly.assertThat(httpRequest.getHttpMethod()).isEqualTo(HttpMethod.POST);
            softly.assertThat(httpRequest.getUriPath()).isEqualTo("/index.html");
            softly.assertThat(httpRequest.getHttpVersion()).isEqualTo(HttpVersion.HTTP_11);
            softly.assertThat(httpRequest.getAccepts()).usingRecursiveComparison().isEqualTo(
                    List.of(
                            new Accept("text/html", 0.8),
                            new Accept("application/json", 0.5),
                            new Accept("*/*", 0.1)
                    )
            );
            softly.assertThat(httpRequest.getBody(ContentType.TEXT_PLAIN).get("body")).isEqualTo("Hello world!");
        });
    }
}
