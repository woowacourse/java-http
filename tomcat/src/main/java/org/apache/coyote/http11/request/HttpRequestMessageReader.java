package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Logger;
import org.apache.coyote.http11.common.Constants;

public class HttpRequestMessageReader {

    private HttpRequestMessageReader() {
    }

    private static final Logger log = Logger.getLogger(HttpRequestMessageReader.class.getName());

    public static HttpRequest read(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder builder = new StringBuilder();

        readUntilHeaderEnds(reader, builder);
        HttpRequest request = HttpRequest.from(builder.toString());
        if (request.hasBody()) {
            addRequestBody(request, reader);
        }
        return request;
    }

    private static void readUntilHeaderEnds(BufferedReader reader, StringBuilder sb) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append(Constants.CRLF);
            if (line.isEmpty()) {
                break;
            }
        }
    }

    private static void addRequestBody(HttpRequest request, BufferedReader reader) throws IOException {
        int contentLength = request.getContentLength();
        char[] buffer = new char[contentLength];
        if (reader.read(buffer) != contentLength) {
            log.warning("Content-Length와 실제 body 길이가 다릅니다.");
        }
        request.setBody(new String(buffer));
    }
}
