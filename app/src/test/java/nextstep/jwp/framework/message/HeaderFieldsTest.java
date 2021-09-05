package nextstep.jwp.framework.message;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;

import static org.assertj.core.api.Assertions.assertThat;

class HeaderFieldsTest {

    @DisplayName("HeaderFields 를 생성한다.")
    @Test
    void create() {
        // given
        LinkedHashMap<String, String> headerParams = new LinkedHashMap<>();
        headerParams.put("Date", "Mon, 10 Jul 2000 01:40:10 GMT");
        headerParams.put("Server", "Apache");
        headerParams.put("Content-Type", "text/html;charset=utf-8");
        headerParams.put("Content-Length", "12");

        // when
        HeaderFields headerFields = HeaderFields.from(headerParams);

        // then
        assertThat(headerFields.toMap()).isEqualTo(headerParams);
    }

    @DisplayName("비어있는 HeaderFields 를 생성한다.")
    @Test
    void createEmpty() {
        // given, when
        HeaderFields emptyHeaderFields = HeaderFields.empty();
        HeaderFields emptyHeaderFieldsFromMap = HeaderFields.from(new LinkedHashMap<>());
        HeaderFields emptyHeaderFieldsFromString = HeaderFields.from("");

        // then
        assertThat(emptyHeaderFields.asString()).isEmpty();
        assertThat(emptyHeaderFields).isEqualTo(emptyHeaderFieldsFromMap);
        assertThat(emptyHeaderFields).isEqualTo(emptyHeaderFieldsFromString);
    }

    @DisplayName("문자열로 HeaderFields 를 생성한다.")
    @Test
    void createWithString() {
        // given
        String headerMessage = String.join("\r\n",
                "Date: Mon, 10 Jul 2000 01:40:10 GMT ",
                "Server: Apache ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "");

        LinkedHashMap<String, String> headerParams = new LinkedHashMap<>();
        headerParams.put("Date", "Mon, 10 Jul 2000 01:40:10 GMT");
        headerParams.put("Server", "Apache");
        headerParams.put("Content-Type", "text/html;charset=utf-8");
        headerParams.put("Content-Length", "12");

        HeaderFields expect = HeaderFields.from(headerParams);

        // when
        HeaderFields headerFields = HeaderFields.from(headerMessage);

        // then
        assertThat(headerFields).isEqualTo(expect);
    }

    @DisplayName("HeaderFields 를 문자열로 변환한다.")
    @Test
    void asString() {
        // given
        LinkedHashMap<String, String> headerParams = new LinkedHashMap<>();
        headerParams.put("Date", "Mon, 10 Jul 2000 01:40:10 GMT");
        headerParams.put("Server", "Apache");
        headerParams.put("Content-Type", "text/html;charset=utf-8");
        headerParams.put("Content-Length", "12");

        String expected = "Date: Mon, 10 Jul 2000 01:40:10 GMT\r\n" +
                "Server: Apache\r\n" +
                "Content-Type: text/html;charset=utf-8\r\n" +
                "Content-Length: 12\r\n";

        HeaderFields headerFields = HeaderFields.from(headerParams);

        // when
        String headerMesssage = headerFields.asString();

        // then
        assertThat(headerMesssage).isEqualTo(expected);
    }
}
