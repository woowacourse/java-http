package nextstep.jwp.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.model.Request;
import nextstep.jwp.model.RequestBody;
import nextstep.jwp.model.RequestLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestAssembler {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestAssembler.class);

    private RequestAssembler() {
    }

    public static Request assemble(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line = reader.readLine();
        LOGGER.debug(line);
        RequestLine requestLine = new RequestLine(line);
        Map<String, String> headers = headers(reader, line);
        RequestBody requestBody = requestBody(reader, headers);
        return new Request(requestLine, headers, requestBody);
    }

    private static RequestBody requestBody(BufferedReader reader, Map<String, String> headers) throws IOException {
        String body = "";
        if (headers.containsKey("Content-Length")) {
            int contentLength = Integer.parseInt(headers.get("Content-Length").trim());
            char[] buffer = new char[contentLength];
            reader.read(buffer, 0, contentLength);
            body = new String(buffer);
        }
        LOGGER.debug(body);
        return new RequestBody(body);
    }

    private static Map<String, String> headers(BufferedReader reader, String line) throws IOException {
        Map<String, String> headers = new HashMap<>();
        while (!"".equals(line)) {
            line = reader.readLine();
            LOGGER.debug(line);
            String[] parsedLine = line.split(": ");
            if (parsedLine.length > 1) {
                headers.put(parsedLine[0], parsedLine[1]);
            }
        }
        return headers;
    }
}
