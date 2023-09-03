package org.apache.coyote.http11.web;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import org.apache.coyote.http11.mvc.view.StaticResourceView;
import org.apache.coyote.http11.mvc.view.View;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayName("View 테스트")
class ViewTest {

    @Test
    void 지정한_리소스를_읽는다() throws IOException {
        // given
        final View view = StaticResourceView.of("/index.html");

        // when
        final String resource = view.renderView();

        // then
        assertThat(resource).contains("<title>대시보드</title>");
    }
}
