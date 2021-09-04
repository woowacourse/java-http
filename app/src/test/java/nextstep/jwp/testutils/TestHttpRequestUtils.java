package nextstep.jwp.testutils;

import nextstep.jwp.http.HttpTranslator;
import nextstep.jwp.http.common.HttpStatusCode;
import nextstep.jwp.http.message.MessageBody;
import nextstep.jwp.http.message.request.HttpRequestMessage;
import nextstep.jwp.http.message.response.HttpResponseMessage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static org.assertj.core.api.Assertions.assertThat;

public class TestHttpRequestUtils {

    public static HttpRequestMessage makeRequest(String requestMessage) throws IOException {
        HttpTranslator httpTranslator = new HttpTranslator(
                generateInputStream(requestMessage), generateOutputStream()
        );
        return httpTranslator.translate();
    }

    private static InputStream generateInputStream(String requestMessage) {
        return new ByteArrayInputStream(requestMessage.getBytes());
    }

    private static OutputStream generateOutputStream() {
        return new ByteArrayOutputStream();
    }
}
