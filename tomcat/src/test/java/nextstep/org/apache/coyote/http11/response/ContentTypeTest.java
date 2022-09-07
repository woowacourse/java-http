package nextstep.org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.*;

import org.apache.coyote.http11.response.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ContentTypeTest {

    @Test
    @DisplayName("uri에 맞는 content type을 반환한다.")
    void findContentType() {
        String expected = "text/html;charset=utf-8";
        String actual = ContentType.findContentType("/index.html");

        assertThat(actual).isEqualTo(expected);
    }

}
