package nextstep.jwp.http.utils;

import nextstep.jwp.http.message.MessageBody;
import nextstep.jwp.http.message.request.RequestHeader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class HttpParseUtilsTest {

    @DisplayName("FormData 를 Map<String, String> 형식으로 추출한다.")
    @Test
    void extractFormData() {
        // given
        Map<String, String> expected = Map.of("account", "ggyool", "password", "password", "email", "ggyool@never.com");

        String formData = "account=ggyool&password=password&email=ggyool@never.com";
        MessageBody messageBody = new MessageBody(formData);

        // when
        Map<String, String> formParams = HttpParseUtils.extractFormData(messageBody);

        // then
        assertThat(formParams).containsAllEntriesOf(expected);
    }

    @DisplayName("Http 요청 메시지에서 header 와 body 부분을 분리한다.")
    @Test
    void extractHeaderAndBodyMessage() throws IOException {
        // given
        BufferedReader bufferedReader = makePostRequestReader();
        RequestHeader requestHeader = HttpParseUtils.extractHeaderMessage(bufferedReader);
        MessageBody messageBody = HttpParseUtils.extractBodyMessage(bufferedReader, requestHeader.takeMessageBodyLength());

        // when, then
        assertThat(requestHeader).isEqualTo(RequestHeader.from(headerMessage()));
        assertThat(messageBody).isEqualTo(new MessageBody(bodyMessage()));
    }

    private BufferedReader makePostRequestReader() {
        String requestMessage = String.join("\r\n",
                headerMessage(),
                "",
                bodyMessage());

        InputStream inputStream = new ByteArrayInputStream(requestMessage.getBytes());
        return new BufferedReader(new InputStreamReader(inputStream));
    }

    private String headerMessage() {
        return String.join("\r\n",
                "POST /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 12 ");
    }

    private String bodyMessage() {
        return "hello world!";
    }
}