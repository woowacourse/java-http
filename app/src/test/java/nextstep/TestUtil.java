package nextstep;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import nextstep.jwp.HttpRequest;

public class TestUtil {

    public static HttpRequest createRequest(String startLine) throws IOException {
        String requestMessage = startLine + System.lineSeparator() +
            "Host: localhost:8080" + System.lineSeparator() +
            "Connection: keep-alive" + System.lineSeparator() +
            "Accept: */*";

        InputStream inputStream = new ByteArrayInputStream(requestMessage.getBytes());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        return new HttpRequest(bufferedReader);
    }

}
