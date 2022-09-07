package nextstep.org.apache.coyote.http11.http11.request.requestline;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.apache.coyote.domain.request.requestline.Path;
import org.junit.jupiter.api.Test;

public class PathTest {

    @Test
    void createPath() {
        // given
        String pathUrl = "/login?account=gugu&password=password";
        // when
        Path path = Path.from(pathUrl);
        // then
        assertAll(
                () -> assertThat(path.getPath()).isEqualTo("/login"),
                () -> assertThat(path.getQueryParam().getQueryValue("account")).isEqualTo("gugu"),
                () -> assertThat(path.getQueryParam().getQueryValue("password")).isEqualTo("password")
        );
    }
}
