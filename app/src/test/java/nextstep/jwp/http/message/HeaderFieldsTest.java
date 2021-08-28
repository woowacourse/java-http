package nextstep.jwp.http.message;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;

import static org.assertj.core.api.Assertions.assertThat;

class HeaderFieldsTest {

    @DisplayName("HeaderFields 를 생성한다.")
    @Test
    void create() {
        // given
        String headerFieldLines = String.join("\r\n",
                "Date: Mon, 10 Jul 2000 01:40:10 GMT ",
                "Server: Apache ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "");

        LinkedHashMap<String, String> expected = new LinkedHashMap<>();
        expected.put("Date", "Mon, 10 Jul 2000 01:40:10 GMT");
        expected.put("Server", "Apache");
        expected.put("Content-Type", "text/html;charset=utf-8");
        expected.put("Content-Length", "12");

        // when
        HeaderFields headerFields = HeaderFields.from(headerFieldLines);

        // then
        assertThat(headerFields.getFields()).containsExactlyEntriesOf(expected);
    }

    @DisplayName("HeaderFields 를 문자열로 변환한다.")
    @Test
    void asString() {
        // given
        LinkedHashMap<String, String> fields = new LinkedHashMap<>();
        fields.put("Date", "Mon, 10 Jul 2000 01:40:10 GMT");
        fields.put("Server", "Apache");
        fields.put("Content-Type", "text/html;charset=utf-8");
        fields.put("Content-Length", "12");

        String expected = "Date: Mon, 10 Jul 2000 01:40:10 GMT \r\n" +
                "Server: Apache \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 12 \r\n";

        HeaderFields headerFields = new HeaderFields(fields);

        // when
        String headerFieldLines = headerFields.asString();

        // then
        assertThat(headerFieldLines).isEqualTo(expected);
    }
}