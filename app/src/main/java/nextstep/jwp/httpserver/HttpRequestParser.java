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
        final Body body = extractRequestBody(startLine, headers, bufferedReader);

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

    private static Body extractRequestBody(StartLine startLine, Headers headers, BufferedReader bufferedReader) throws IOException {
        final Map<String, String> param = new HashMap<>();
        int contentLength = Integer.parseInt(headers.contentLength());

        if (startLine.isPost() && contentLength > 0) {
            getParameter(bufferedReader, param, contentLength);
        }

        return new Body(param);
    }

    private static void getParameter(BufferedReader bufferedReader, Map<String, String> param, int contentLength) throws IOException {
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        String requestBody = new String(buffer);

        String[] parameters = requestBody.split("&");
        for (String parameter : parameters) {
            String[] keyValue = parameter.split("=");
            param.put(keyValue[0], keyValue[1]);
        }
    }
}
