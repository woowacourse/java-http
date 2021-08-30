package nextstep.jwp.http;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("RequestHeaderTest")
class RequestHeaderTest {

    private static final String ACCEPT = "Accept";

    @Test
    @DisplayName("있는 데이터를 꺼내면 정상적으로 반환된다.")
    void getParameter() {
        // given
        RequestHeader requestHeader = getHeader("text/html");
        // when
        String parameter = requestHeader.getParameter(ACCEPT);
        // then
        assertThat(parameter).isEqualTo("text/html");
    }

    @Test
    @DisplayName("없는 데이터를 꺼내면 null 로 반환된다.")
    void getParameterWhenNull() {
        // given
        RequestHeader requestHeader = getHeader("text/html");
        // when
        String parameter = requestHeader.getParameter("abc");
        // then
        assertThat(parameter).isNull();
    }

    @ParameterizedTest
    @CsvSource(value = {"html:text/html", "css:text/css", "javascript:application/javascript"}, delimiter = ':')
    @DisplayName("Accept 에 html 이 포함되어 있다면 text/html 을 반환한다.")
    void acceptTypeWhenHtml(String target, String expected) {
        // given
        RequestHeader requestHeader = getHeader(target);
        // when
        String accept = requestHeader.acceptType();
        // then
        assertThat(accept).isEqualTo(expected);
    }

    private RequestHeader getHeader(String accept) {
        Map<String, String> header = new HashMap<>();
        header.put(ACCEPT, accept);
        return new RequestHeader(header);
    }
}