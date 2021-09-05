package nextstep.jwp.framework.message.response;

import nextstep.jwp.framework.message.HeaderFields;
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

    @DisplayName("equals 와 hashCode 검증")
    @Test
    void equalsAndHashCode() {
        // given
        HeaderFields headerFields = headerFields();
        ResponseHeader responseHeader = new ResponseHeader(headerFields);
        ResponseHeader otherResponseHeader = new ResponseHeader(headerFields);

        // then
        assertThat(responseHeader).isEqualTo(otherResponseHeader)
                .hasSameHashCodeAs(otherResponseHeader);
    }

    private HeaderFields headerFields() {
        LinkedHashMap<String, String> headerParams = new LinkedHashMap<>();
        headerParams.put("Content-Type", "text/html;charset=utf-8");
        headerParams.put("Content-Length", "5564");
        return HeaderFields.from(headerParams);
    }
}
