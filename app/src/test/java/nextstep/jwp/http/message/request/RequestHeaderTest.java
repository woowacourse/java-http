package nextstep.jwp.http.message.request;

import nextstep.jwp.http.message.HeaderFields;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class RequestHeaderTest {

    @DisplayName("RequestHeader 를 생성한다.")
    @Test
    void create() {
        // given
        HeaderFields headerFields = headerFieldsWhenExistsBody();

        // when
        RequestHeader requestHeader = new RequestHeader(headerFields);

        // then
        assertThat(requestHeader.getHeaderFields()).isEqualTo(headerFields);
    }

    @DisplayName("RequestHeader 를 문자열로 변환한다.")
    @Test
    void convertToString() {
        // given
        String expect = String.join("\r\n",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Content-Length: 10",
                "");

        RequestHeader requestHeader = new RequestHeader(headerFieldsWhenExistsBody());

        // when
        String headerMessage = requestHeader.convertToString();
        
        // then
        assertThat(headerMessage).isEqualTo(expect);
    }

    @DisplayName("Message Body Length 를 알아낸다 - Body 가 있는 경우")
    @Test
    void takeContentLengthWhenExistsBody() {
        // given
        RequestHeader requestHeader = new RequestHeader(headerFieldsWhenExistsBody());

        // when
        int contentLength = requestHeader.takeContentLength();

        // then
        assertThat(contentLength).isEqualTo(10);
    }

    @DisplayName("Message Body Length 를 알아낸다 - Body 가 없는 경우")
    @Test
    void takeContentLengthWhenNoBody() {
        // given
        RequestHeader requestHeader = new RequestHeader(headerFieldsWhenNoBody());

        // when
        int contentLength = requestHeader.takeContentLength();

        // then
        assertThat(contentLength).isEqualTo(0);
    }

    private HeaderFields headerFieldsWhenExistsBody() {
        Map<String, String> headerParams = new LinkedHashMap<>();
        headerParams.put("Host", "localhost:8080");
        headerParams.put("Connection", "keep-alive");
        headerParams.put("Content-Length", "10");
        HeaderFields headerFields = new HeaderFields(headerParams);
        return headerFields;
    }

    private HeaderFields headerFieldsWhenNoBody() {
        Map<String, String> headerParams = new LinkedHashMap<>();
        headerParams.put("Host", "localhost:8080");
        headerParams.put("Connection", "keep-alive");
        HeaderFields headerFields = new HeaderFields(headerParams);
        return headerFields;
    }
}
