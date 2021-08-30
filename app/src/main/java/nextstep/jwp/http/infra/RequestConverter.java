package nextstep.jwp.http.infra;

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
        String[] requestLine = splitRequestHeaders[0].split(" ");

        return new HttpRequest.Builder(bufferedReader)
                .method(requestLine[0])
                .uri(requestLine[1])
                .requestHeaders(splitRequestHeaders)
                .requestBody()
                .build();
    }
}
