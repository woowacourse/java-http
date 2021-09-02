package nextstep.jwp.http;

import nextstep.jwp.http.common.PathUtils;
import nextstep.jwp.http.message.element.Body;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

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

        String actual = body.getValue()
            .orElseThrow(IllegalArgumentException::new);

        assertThat(actual).isEqualTo("test");
    }

    @Test
    void asString() {
        Body body = new Body("test");

        String actual = body.asString();

        assertThat(actual).isEqualTo("test" + Protocol.LINE_SEPARATOR.value());
    }

    @Test
    void length() {
        Body body = new Body("test");

        assertThat(body.length()).isEqualTo(4);
    }
}
