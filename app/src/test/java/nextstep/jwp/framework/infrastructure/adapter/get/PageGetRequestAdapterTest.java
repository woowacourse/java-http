package nextstep.jwp.framework.infrastructure.adapter.get;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import nextstep.common.TestUtil;
import nextstep.jwp.framework.infrastructure.adapter.RequestAdapter;
import nextstep.jwp.framework.infrastructure.config.FactoryConfiguration;
import nextstep.jwp.framework.infrastructure.http.request.HttpRequest;
import nextstep.jwp.framework.infrastructure.http.request.HttpRequestBody;
import nextstep.jwp.framework.infrastructure.http.request.HttpRequestHeader;
import nextstep.jwp.framework.infrastructure.http.response.HttpResponse;
import nextstep.jwp.framework.infrastructure.http.status.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("PageGetRequestAdapter 단위 테스트")
class PageGetRequestAdapterTest {

    /*
    / -> /index.html
    /login -> /login.html
    /register -> /login.html
     */
    @DisplayName("GetMapping Url로 들어온 요청을 적합한 View 파일로 변환한다.")
    @Test
    void it_returns_proper_view_page() {
        // given
        HttpRequestHeader header = HttpRequestHeader.from(Arrays.asList("GET /login HTTP/1.1"));
        HttpRequest httpRequest = new HttpRequest(header, new HttpRequestBody(null));
        RequestAdapter adapter = FactoryConfiguration.requestMapping().findAdapter(httpRequest);

        // when
        HttpResponse httpResponse = adapter.doService(httpRequest);

        // then
        assertThat(httpResponse.getResponseBody())
            .isEqualTo(TestUtil.writeResponse("/login.html", HttpStatus.OK));
    }
}
