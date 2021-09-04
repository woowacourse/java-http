package nextstep.jwp.testutils;

import nextstep.jwp.framework.HttpTranslator;
import nextstep.jwp.framework.message.request.HttpRequestMessage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
