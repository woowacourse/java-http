package nextstep.jwp.httpserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import nextstep.jwp.httpserver.domain.Body;
import nextstep.jwp.httpserver.domain.Headers;
import nextstep.jwp.httpserver.domain.request.HttpRequest;
import nextstep.jwp.httpserver.domain.request.StartLine;

public class HttpRequestParser {
    private static final String LAST_HEADER_SYMBOL = "";
    private static final String HEADER_DIVIDER = ":";

    public static HttpRequest parse(InputStream inputStream) throws IOException {
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        final StartLine startLine = StartLine.from(bufferedReader.readLine());
        final Headers headers = extractAllHeaders(bufferedReader);
        // TODO POST 시 채우기
        final Body body = new Body();

        return new HttpRequest(startLine, headers, body);
    }

    private static Headers extractAllHeaders(BufferedReader bufferedReader) throws IOException {
        final Map<String, String> headers = new HashMap<>();

        String line = bufferedReader.readLine();
        while (!LAST_HEADER_SYMBOL.equals(line)) {
            int index = line.indexOf(HEADER_DIVIDER);
            headers.put(line.substring(0, index), line.substring(index + 2));
            line = bufferedReader.readLine();
            if (line == null) {
                break;
            }
        }

        return new Headers(headers);
    }
}
