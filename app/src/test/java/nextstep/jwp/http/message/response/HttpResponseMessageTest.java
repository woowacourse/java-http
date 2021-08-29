package nextstep.jwp.http.message.response;

import nextstep.jwp.http.common.HttpStatusCode;
import nextstep.jwp.http.message.HeaderFields;
import nextstep.jwp.http.message.MessageBody;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;

import static org.assertj.core.api.Assertions.assertThat;

class HttpResponseMessageTest {

    private static final String HTTP_VERSION = "HTTP/1.1";

    private HeaderFields headerFields;
    private final String bodyString = "Hello world!";

    @BeforeEach
    void setUp() {
        LinkedHashMap<String, String> fields = new LinkedHashMap<>();
        fields.put("Content-Type", "text/html;charset=utf-8");
        fields.put("Content-Length", "12");
        headerFields = new HeaderFields(fields);
    }

    @DisplayName("Header와 Body가 둘 다 존재하는 HttpRequestMessage 생성")
    @Test
    void createWithBody() {
        // given
        ResponseHeader expectedResponseHeader = new ResponseHeader(HTTP_VERSION, HttpStatusCode.OK, headerFields);
        MessageBody expectedMessageBody = new MessageBody(bodyString);

        // when
        HttpResponseMessage httpResponseMessage = new HttpResponseMessage(
                HTTP_VERSION, HttpStatusCode.OK, headerFields, bodyString.getBytes()
        );

        // then
        assertThat(httpResponseMessage.getHeader()).isEqualTo(expectedResponseHeader);
        assertThat(httpResponseMessage.getBody()).isEqualTo(expectedMessageBody);
    }

    @DisplayName("HttpResponseMessage를 byte 배열로 만든다.")
    @Test
    void toBytes() {
        // given
        String expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!");

        HttpResponseMessage httpResponseMessage = new HttpResponseMessage(
                HTTP_VERSION, HttpStatusCode.OK, headerFields, bodyString.getBytes()
        );

        // when, then
        assertThat(httpResponseMessage.toBytes()).isEqualTo(expected.getBytes());
    }
}