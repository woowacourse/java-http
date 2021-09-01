package nextstep.jwp.web.http.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ContentTypeTest {

    @DisplayName("알맞은 컨텐트 타입을 리턴하는지 확인")
    @Test
    void getTextTest() {
        //given
        //when
        //then
        assertThat(ContentType.HTML.getMimeType()).contains("text/html");
        assertThat(ContentType.JS.getMimeType()).contains("text/js");
        assertThat(ContentType.PLAIN_TEXT.getMimeType()).contains("text/plain");
        assertThat(ContentType.CSS.getMimeType()).contains("text/css");
    }
}