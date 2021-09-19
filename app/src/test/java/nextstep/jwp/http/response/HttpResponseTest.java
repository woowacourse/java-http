package nextstep.jwp.http.response;

import static org.assertj.core.api.Assertions.assertThat;

import nextstep.jwp.http.ResponseHeaders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpResponseTest {

    @DisplayName("response 의 헤더들을 가져오기")
    @Test
    void getHeaders() {
        final ResponseHeaders responseHeaders = new ResponseHeaders();
        final HttpResponse httpResponse = new HttpResponse(responseHeaders);

        httpResponse.addHeaders("test", "test-value");

        final ResponseHeaders actual = httpResponse.getHeaders();
        assertThat(actual).isEqualTo(responseHeaders);
        assertThat(actual.getHeaders())
                .containsEntry("test", "test-value");
    }
}