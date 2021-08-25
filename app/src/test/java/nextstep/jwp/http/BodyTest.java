package nextstep.jwp.http;

import static org.assertj.core.api.Assertions.assertThat;

import nextstep.jwp.http.common.PathUtils;
import org.junit.jupiter.api.Test;

class BodyTest {

    @Test
    void empty() {
        Body body = Body.empty();

        assertThat(body.asString()).isBlank();
    }

    @Test
    void fromFile() {
        Body body = Body.fromFile(PathUtils.toFile("test.html"));

        assertThat(body.asString()).contains("<title>대시보드</title>");
    }

    @Test
    void isEmpty() {
        Body body = Body.empty();

        assertThat(body.isEmpty()).isTrue();
    }

    @Test
    void getBody() {
        Body body = new Body("test");

        String actual = body.getBody()
            .orElseThrow(IllegalArgumentException::new);

        assertThat(actual).isEqualTo("test");
    }

    @Test
    void asString() {
        Body body = new Body("test");

        String actual = body.asString();

        assertThat(actual).isEqualTo("test" + Protocol.LINE_SEPARATOR);
    }

    @Test
    void length() {
        Body body = new Body("test");

        assertThat(body.length()).isEqualTo(4);
    }
}