package nextstep.jwp.framework.infrastructure.adapter.post;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import nextstep.common.TestUtil;
import nextstep.jwp.framework.infrastructure.adapter.RequestAdapter;
import nextstep.jwp.framework.config.FactoryConfiguration;
import nextstep.jwp.framework.infrastructure.http.request.HttpRequest;
import nextstep.jwp.framework.infrastructure.http.request.HttpRequestBody;
import nextstep.jwp.framework.infrastructure.http.request.HttpRequestHeader;
import nextstep.jwp.framework.infrastructure.http.response.HttpResponse;
import nextstep.jwp.framework.infrastructure.http.status.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("RegisterRequestAdapter 단위 테스트")
class RegisterRequestAdapterTest {

    @DisplayName("회원가입에 성공하면 index 페이지로 302 리다이렉트된다.")
    @Test
    void it_redirects_when_register_success() {
        // given
        HttpRequestHeader header = HttpRequestHeader.from(Arrays.asList("POST /register HTTP/1.1"));
        String body = "account=gu123gu&password=password&email=kevin@naver.com";
        HttpRequest httpRequest = new HttpRequest(header, new HttpRequestBody(body));
        RequestAdapter adapter = FactoryConfiguration.requestMapping().findAdapter(httpRequest);

        // when
        HttpResponse httpResponse = adapter.doService(httpRequest);

        // then
        assertThat(httpResponse.getResponseBody())
            .isEqualTo(TestUtil.writeResponse("/index.html", HttpStatus.FOUND));
    }

    @DisplayName("회원가입에 실패하면 401 페이지로 이동한다.")
    @Test
    void it_returns_401_when_register_fail() {
        // given
        HttpRequestHeader header = HttpRequestHeader.from(Arrays.asList("POST /register HTTP/1.1"));
        String body = "account=gugu&password=passwoadfadfrd";
        HttpRequest httpRequest = new HttpRequest(header, new HttpRequestBody(body));
        RequestAdapter adapter = FactoryConfiguration.requestMapping().findAdapter(httpRequest);

        // when
        HttpResponse httpResponse = adapter.doService(httpRequest);

        // then
        assertThat(httpResponse.getResponseBody())
            .isEqualTo(TestUtil.writeResponse("/401.html", HttpStatus.UNAUTHORIZED));
    }
}
