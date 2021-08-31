package nextstep.jwp.web.http.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("HTTP Response 관련 로직을 테스트한다.")
class HttpResponseTest {

    @Nested
    class StatusLineTest {

        @DisplayName("Status Code를 추가하면 그에 맞는 Status Line 이 생성된다. - 200 OK")
        @Test
        void responseStatusLine_200OK_Success() {
            // given
            HttpResponse response = new HttpResponse();
            String expected = "HTTP/1.1 200 OK";

            // when
            response.setStatusLine(StatusCode.OK);
            String actual = response.asString();

            // then
            assertThat(actual).contains(expected);
        }

        @DisplayName("Status Code를 추가하면 그에 맞는 Status Line 이 생성된다. - 302 Found")
        @Test
        void responseStatusLine_302Found_Success() {
            // given
            HttpResponse response = new HttpResponse();
            String expected = "HTTP/1.1 302 Found";

            // when
            response.setStatusLine(StatusCode.FOUND);
            String actual = response.asString();

            // then
            assertThat(actual).contains(expected);
        }

        @DisplayName("Status Code를 추가하면 그에 맞는 Status Line 이 생성된다. - 401 Unauthorized")
        @Test
        void responseStatusLine_401Unauthorized_Success() {
            // given
            HttpResponse response = new HttpResponse();
            String expected = "HTTP/1.1 401 Unauthorized";

            // when
            response.setStatusLine(StatusCode.UNAUTHORIZED);
            String actual = response.asString();

            // then
            assertThat(actual).contains(expected);
        }
    }

    @Nested
    class ResponseHeadersTest {

        @DisplayName("헤더를 추가하면 Http 응답 형식에 맞게 출력된다.")
        @Test
        void addHeaders_Success() {
            // given
            HttpResponse response = new HttpResponse();
            response.setStatusLine(StatusCode.OK);

            // when
            response.addHeader("Content-Length", "80");
            String expected = String.join("\r\n",
                "HTTP/1.1 200 OK",
                "Content-Length: 80");
            String actual = response.asString();

            // then
            assertThat(actual).contains(expected);
        }

        @DisplayName("두개 이상의 헤더를 추가하면 Http 응답 형식에 맞게 출력된다.")
        @Test
        void addHeaders_multipleHeaders_Success() {
            // given
            HttpResponse response = new HttpResponse();
            response.setStatusLine(StatusCode.OK);

            // when
            response.addHeader("Content-Length", "80");
            response.addHeader("Content-Type", ContentType.JS.getValue());
            String expected = String.join("\r\n",
                "HTTP/1.1 200 OK",
                "Content-Length: 80",
                "Content-Type: " + ContentType.JS.getValue());
            String actual = response.asString();

            // then
            assertThat(actual).contains(expected);
        }
    }

    @Nested
    class ResponseBodyTest {

        @DisplayName("응답 body를 추가하면 \r\n 이후에 응답 body가 함께 출력된다.")
        @Test
        void addBody_Success() throws IOException {
            // given
            String body = readIndexHtml();
            HttpResponse response = new HttpResponse();
            response.setStatusLine(StatusCode.OK);
            response.addHeader("Content-Type", ContentType.HTML.getValue());
            response.addBody(body);

            // when
            String expected = String.join("\r\n",
                "HTTP/1.1 200 OK",
                "Content-Type: " + ContentType.HTML.getValue(),
                "",
                body);
            String actual = response.asString();

            // then
            assertThat(actual).contains(expected)
                .contains("대시보드");
        }

        private String readIndexHtml() throws IOException {
            InputStream in = getClass().getResourceAsStream("/static/index.html");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));

            StringBuilder sb = new StringBuilder();

            while (bufferedReader.ready()) {
                sb.append(bufferedReader.readLine())
                    .append("\r\n");
            }

            return sb.toString();
        }
    }
}
