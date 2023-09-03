package org.apache.coyote.handle.mapping;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import org.apache.coyote.request.HttpRequest;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HelloWorldMappingInfoTest {

    @Nested
    class 요청_매핑_여부_확인 {

        @Test
        void 요청_매핑이_가능하면_true_반환한다() throws Exception {
            final String httpRequestMessage = String.join("\r\n",
                    "GET / HTTP/1.1",
                    "Host: localhost:8080",
                    "Connection: keep-alive",
                    "Content-Length: 12",
                    "Accept: */*;q=0.1, text/html;q=0.8, application/json;q=0.5"
            );
            final ByteArrayInputStream inputStream = new ByteArrayInputStream(httpRequestMessage.getBytes());
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            final HttpRequest httpRequest = HttpRequest.parse(bufferedReader);

            final HelloWorldMappingInfo helloWorldMappingInfo = new HelloWorldMappingInfo();
            final boolean result = helloWorldMappingInfo.support(httpRequest);

            assertThat(result).isTrue();
        }

        @Test
        void 요청_매핑이_가능하지_않다면_false_반환한다() throws Exception {
            final String httpRequestMessage = String.join("\r\n",
                    "POST / HTTP/1.1",
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

            final HelloWorldMappingInfo helloWorldMappingInfo = new HelloWorldMappingInfo();
            final boolean result = helloWorldMappingInfo.support(httpRequest);

            assertThat(result).isFalse();
        }
    }
}
