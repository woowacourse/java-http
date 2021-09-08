package nextstep.jwp.framework.message.request;

import nextstep.jwp.framework.common.HttpMethod;
import nextstep.jwp.framework.common.HttpUri;
import nextstep.jwp.framework.common.HttpVersion;
import nextstep.jwp.framework.exception.HttpMessageConvertFailureException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RequestLineTest {

    @DisplayName("RequestLine 을 생성한다.")
    @Test
    void create() {
        // given
        HttpMethod httpMethod = HttpMethod.POST;
        HttpUri httpUri = new HttpUri("/login");
        HttpVersion httpVersion = HttpVersion.HTTP_1_1;

        // when
        RequestLine requestLine = RequestLine.of(httpMethod, httpUri, httpVersion);

        // then
        assertThat(requestLine.getHttpMethod()).isEqualTo(httpMethod);
        assertThat(requestLine.getHttpUri()).isEqualTo(httpUri);
        assertThat(requestLine.getHttpVersion()).isEqualTo(httpVersion);
    }

    @DisplayName("문자열로 RequestLine 을 생성한다.")
    @Test
    void createFromString() {
        // when
        RequestLine requestLine = RequestLine.from("POST /login HTTP/1.1");

        // then
        assertThat(requestLine.getHttpMethod()).isEqualTo(HttpMethod.POST);
        assertThat(requestLine.getHttpUri()).isEqualTo(new HttpUri("/login"));
        assertThat(requestLine.getHttpVersion()).isEqualTo(HttpVersion.HTTP_1_1);
    }

    @DisplayName("잘못된 문자열로 RequestLine 을 생성한다.")
    @Test
    void createFromInvalidString() {
        // when, then
        assertThatThrownBy(() -> RequestLine.from("OK POST /login HTTP/1.1"))
                .isInstanceOf(HttpMessageConvertFailureException.class);
    }

    @DisplayName("RequestLine 을 문자열로 반환한다. (마지막에 NewLine 포함)")
    @Test
    void asSting() {
        // given
        String expect = "POST /login HTTP/1.1\r\n";
        RequestLine requestLine = RequestLine.of(HttpMethod.POST, new HttpUri("/login"), HttpVersion.HTTP_1_1);

        // when, then
        assertThat(requestLine.asString()).isEqualTo(expect);
    }

    @DisplayName("RequestLine 을 바이트 배열로 변환 (마지막에 NewLine 포함)")
    @Test
    void toBytes() {
        // given
        byte[] expect = "POST /login HTTP/1.1\r\n".getBytes();
        RequestLine requestLine = RequestLine.of(HttpMethod.POST, new HttpUri("/login"), HttpVersion.HTTP_1_1);

        // when, then
        assertThat(requestLine.toBytes()).isEqualTo(expect);
    }

    @DisplayName("equals 와 hashCode 검증")
    @Test
    void equalsAndHashCode() {
        // given
        HttpMethod httpMethod = HttpMethod.POST;
        HttpUri httpUri = new HttpUri("/login");
        HttpVersion httpVersion = HttpVersion.HTTP_1_1;

        RequestLine requestLine = RequestLine.of(httpMethod, httpUri, httpVersion);
        RequestLine otherRequestLine = RequestLine.of(httpMethod, httpUri, httpVersion);

        // then
        assertThat(requestLine).isEqualTo(otherRequestLine)
                .hasSameHashCodeAs(otherRequestLine);
    }
}
