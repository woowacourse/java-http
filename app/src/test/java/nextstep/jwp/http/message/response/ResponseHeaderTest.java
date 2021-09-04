package nextstep.jwp.http.message.response;

import nextstep.jwp.http.message.HeaderFields;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;

import static org.assertj.core.api.Assertions.assertThat;

class ResponseHeaderTest {

    @DisplayName("ResponseHeader 를 생성한다.")
    @Test
    void create() {
        // given
        HeaderFields headerFields = headerFields();

        // when
        ResponseHeader responseHeader = new ResponseHeader(headerFields);

        // then
        assertThat(responseHeader.getHeaderFields()).isEqualTo(headerFields);
    }

    @DisplayName("RequestHeader 를 바이트 배열로 변환한다.")
    @Test
    void toBytes() {
        // given
        String headerMessage = "Content-Type: text/html;charset=utf-8\r\n" +
                "Content-Length: 5564\r\n";
        byte[] expect = headerMessage.getBytes();
        ResponseHeader responseHeader = new ResponseHeader(headerFields());

        // when
        byte[] bytes = responseHeader.toBytes();

        // then
        AssertionsForClassTypes.assertThat(bytes).isEqualTo(expect);
    }

    private HeaderFields headerFields() {
        LinkedHashMap<String, String> headerParams = new LinkedHashMap<>();
        headerParams.put("Content-Type", "text/html;charset=utf-8");
        headerParams.put("Content-Length", "5564");
        HeaderFields headerFields = new HeaderFields(headerParams);
        return headerFields;
    }
}
