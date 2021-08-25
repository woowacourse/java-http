package nextstep.jwp.framework.infrastructure.adapter.get;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import nextstep.common.TestUtil;
import nextstep.jwp.framework.infrastructure.http.response.HttpResponse;
import nextstep.jwp.framework.infrastructure.http.status.HttpStatus;
import nextstep.jwp.framework.infrastructure.resolver.StaticFileResolver;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("NotFoundRequestAdapter 단위 테스트")
class NotFoundRequestAdapterTest {

    @DisplayName("요청이 무엇이든지간에 404 페이지를 반환한다.")
    @Test
    void it_returns_404() {
        // given
        NotFoundRequestAdapter notFoundRequestAdapter =
            new NotFoundRequestAdapter(StaticFileResolver.getInstance());

        // when
        HttpResponse httpResponse = notFoundRequestAdapter.doService(null);

        // then
        assertThat(httpResponse.getResponseBody())
            .isEqualTo(TestUtil.writeResponse("/404.html", HttpStatus.NOT_FOUND));
    }
}
