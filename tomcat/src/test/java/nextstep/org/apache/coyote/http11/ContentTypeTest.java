package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http11.model.ContentType;
import org.junit.jupiter.api.Test;

public class ContentTypeTest {

    @Test
    void getContentType() {
        String input = "/index.html";
        ContentType result = ContentType.of(input);

        assertThat(result).isEqualTo(ContentType.HTML);
    }
}
