package http.repsonse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import http.header.HttpHeaders;
import http.response.HttpResponse;
import http.response.HttpStatus;
import org.junit.jupiter.api.Test;

class HttpResponseTest {

    @Test
    void HttpResponse를_형식에_맞게_문자열로_변환한다() {
        // given
        String requestBody = "request body";
        HttpHeaders headers = HttpHeaders.createEmpty();
        headers.add("Content-Type", "text/html");
        headers.add("Content-Length", String.valueOf(requestBody.getBytes().length));
        HttpResponse httpResponse = new HttpResponse(HttpStatus.OK, headers, requestBody);

        // when
        String responseString = httpResponse.toResponseFormat();

        // then
        assertAll(
                () -> assertThat(responseString).contains("HTTP/1.1 200 OK"),
                () -> assertThat(responseString).contains("Content-Length: " + requestBody.getBytes().length),
                () -> assertThat(responseString).contains("Content-Type: text/html"),
                () -> assertThat(responseString).contains("request body")
        );
    }
}
