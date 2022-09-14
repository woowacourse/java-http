package nextstep.jwp.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class FileNameTest {

    @Test
    void parseFileName() {
        // given
        List<String> valid = List.of("GET /index HTTP1.1");

        // when
        FileName fileName = FileName.from(valid);

        // then
        assertThat(fileName.concat()).isEqualTo("/index.html");
    }

    @Test
    void parseFileNameWithQueries() {
        // given
        List<String> valid = List.of("GET /login?id=gugu&password=password HTTP1.1");

        // when
        FileName fileName = FileName.from(valid);

        // then
        assertThat(fileName.concat()).isEqualTo("/login.html");
    }

    @Test
    void parseFileNameDefault() {
        // given
        List<String> valid = List.of("GET / HTTP1.1");

        // when
        FileName fileName = FileName.from(valid);

        // then
        assertThat(fileName.concat()).isEqualTo("Hello world!.html");
    }

    @Test
    void emptyRequest() {
        // given
        List<String> valid = List.of();

        // when then
        assertThatThrownBy(() -> FileName.from(valid))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void invalidRequest() {
        // given
        List<String> valid = List.of("GET/index.html");

        // when then
        assertThatThrownBy(() -> FileName.from(valid))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
