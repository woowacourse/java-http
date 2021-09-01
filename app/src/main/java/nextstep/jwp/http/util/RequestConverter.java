package nextstep.jwp.http.util;

import java.io.BufferedReader;
import java.io.IOException;
import nextstep.jwp.http.request.HttpRequest;

public class RequestConverter {

    private RequestConverter() {
    }

    public static HttpRequest convertToHttpRequest(final BufferedReader bufferedReader)
            throws IOException {
        StringBuilder requestHeaders = new StringBuilder();
        String line = bufferedReader.readLine();
        while (!"".equals(line)) {
            if (line == null) {
                break;
            }
            requestHeaders.append(line).append("\n");
            line = bufferedReader.readLine();
        }

        String[] splitRequestHeaders = requestHeaders.toString().split("\n");
        String requestLine = splitRequestHeaders[0];

        return new HttpRequest.Builder(bufferedReader)
                .requestLine(requestLine)
                .requestHeaders(splitRequestHeaders)
                .requestBody()
                .build();
    }
}
