package nextstep.jwp.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;
import nextstep.jwp.RequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpRequestReader {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    public HttpRequestReader() {
    }

    public static HttpRequest httpRequest(InputStream inputStream) throws IOException {
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        final BufferedReader reader = new BufferedReader(inputStreamReader);

        String line = reader.readLine();

        final String[] request = line.split(" ");
        final String requestMethod = request[0];
        final String requestUri = request[1];

        final Map<String, String> requestHeaders = new LinkedHashMap<>();
        int contentLength = 0;
        while (!"".equals(line = reader.readLine())) {
            if (line == null) {
                break;
            }
            final String[] fields = line.split(": ");
            requestHeaders.put(fields[0], fields[1]);

            if (line.contains("Content-Type")) {
                contentLength = Integer.parseInt(requestHeaders.get("Content-Length"));
            }
        }

        char[] buffer = new char[contentLength];
        reader.read(buffer, 0, contentLength);
        String requestBody = new String(buffer);

        return new HttpRequest(requestMethod, requestUri, requestHeaders, requestBody);
    }
}
