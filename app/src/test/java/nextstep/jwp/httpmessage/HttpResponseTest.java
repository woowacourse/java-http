package nextstep.jwp.httpmessage;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("HttpResponse 테스트")
class HttpResponseTest {

    @Test
    void responseGetStatus() {
        //given
        final HttpResponse httpResponse = new HttpResponse(new StatusLine(HttpVersion.HTTP1_1, HttpStatusCode.OK), new HttpHeaders(Collections.emptyMap()));
        //when
        final String statusLine = httpResponse.getStatusLine();
        //then
        assertThat(statusLine).isEqualTo("HTTP/1.1 200 OK");
        assertThat(httpResponse.getHttpVersion()).isEqualTo(HttpVersion.HTTP1_1);
        assertThat(httpResponse.getHttpStatusCode()).isEqualTo(HttpStatusCode.OK);
    }

    @Test
    void responsePostStatus() {
        //given
        final HttpResponse httpResponse = new HttpResponse(new StatusLine(HttpVersion.HTTP1_1, HttpStatusCode.FOUND), new HttpHeaders(Collections.emptyMap()));
        //when
        final String statusLine = httpResponse.getStatusLine();
        //then
        assertThat(statusLine).isEqualTo("HTTP/1.1 302 Found");
        assertThat(httpResponse.getHttpVersion()).isEqualTo(HttpVersion.HTTP1_1);
        assertThat(httpResponse.getHttpStatusCode()).isEqualTo(HttpStatusCode.FOUND);
    }

    @Test
    void setHeader() {
        //given
        final Map<String, String> headers = Collections.emptyMap();
        final HttpResponse httpResponse = new HttpResponse(new StatusLine(HttpVersion.HTTP1_1, HttpStatusCode.FOUND), new HttpHeaders(headers));
        final String key = "Location";
        final String value = "/index.html";
        //when
        httpResponse.setHeader(key, value);
        //then
        assertThat(httpResponse.getHeader(key)).isEqualTo(value);
    }
}