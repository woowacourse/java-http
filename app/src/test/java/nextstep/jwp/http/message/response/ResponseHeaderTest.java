package nextstep.jwp.http.message.response;

import nextstep.jwp.http.HttpStatusCode;
import nextstep.jwp.http.message.HeaderFields;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;

import static org.assertj.core.api.Assertions.assertThat;

class ResponseHeaderTest {

    @DisplayName("ResponseHeader를 생성하고 문자열로 변환한다.")
    @Test
    void createAndConvertToString() {
        // given
        LinkedHashMap<String, String> fields = new LinkedHashMap<>();
        fields.put("Content-Type", "text/html;charset=utf-8");
        fields.put("Content-Length", "5564");
        HeaderFields headerFields = new HeaderFields(fields);

        String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 5564 \r\n";

        // when
        ResponseHeader responseHeader = new ResponseHeader("HTTP/1.1", HttpStatusCode.OK, headerFields);
        String headerString = responseHeader.convertToString();

        // then
        assertThat(headerString).isEqualTo(expected);
    }
}