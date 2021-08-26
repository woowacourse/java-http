package nextstep.jwp.http.message;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class HeaderFieldsTest {

    @DisplayName("HeaderFields 를 생성한다.")
    @Test
    void create() {
        String headerFieldLines = String.join("\r\n",
                "Date: Mon, 10 Jul 2000 01:40;10 GMT ",
                "Server: Apache ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ");

        Map<String, String> expected = Map.of(
                "Date", "Mon, 10 Jul 2000 01:40;10 GMT",
                "Server", "Apache",
                "Content-Type", "text/html;charset=utf-8",
                "Content-Length", "12"
        );

        HeaderFields headerFields = HeaderFields.from(headerFieldLines);
        assertThat(headerFields.getFields()).containsAllEntriesOf(expected);
    }
}