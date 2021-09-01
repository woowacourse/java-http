package nextstep.jwp.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpHeaderTest {

    @DisplayName("파라미터로 받는 요청헤더에 세션 아이디가 없으면 set-cookie를 포함한다.")
    @Test
    void responseHeader() {
        HttpHeader requestHeader = new HttpHeader(new HashMap<>());
        HttpHeader responseHeader = HttpHeader.getResponseHeader("", ContentType.HTML, requestHeader);
        assertThat(responseHeader.getValueByKey("Set-Cookie")).isNotNull();

        requestHeader.addAttribute("Cookie", "JSESSIONID=sth");
        HttpHeader responseHeaderWithoutCookie = HttpHeader.getResponseHeader("", ContentType.HTML, requestHeader);
        assertThat(responseHeaderWithoutCookie.getValueByKey("Set-Cookie")).isNull();
    }
}