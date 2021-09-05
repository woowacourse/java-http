package nextstep.jwp.http.util;

import java.io.BufferedReader;
import java.io.IOException;
import nextstep.jwp.WebServer;
import nextstep.jwp.http.request.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestConverter {

    private static final String EMPTY = "";

    private static final Logger logger = LoggerFactory.getLogger(WebServer.class);

    private RequestConverter() {
    }

    public static HttpRequest convertToHttpRequest(final BufferedReader bufferedReader)
            throws IOException {
        StringBuilder requestHeaders = new StringBuilder();
        String line = bufferedReader.readLine();
        while (!EMPTY.equals(line)) {
            if (line == null) {
                break;
            }
            requestHeaders.append(line).append("\n");
            line = bufferedReader.readLine();
        }

        String[] splitRequestHeaders = requestHeaders.toString().split("\n");
        String requestLine = splitRequestHeaders[0];
        logger.info("리퀘스트 라인 : {}", requestLine);

        return new HttpRequest.Builder(bufferedReader)
                .requestLine(requestLine)
                .requestHeaders(splitRequestHeaders)
                .requestBody()
                .build();
    }
}
