package nextstep.jwp.web.network.request;

import nextstep.jwp.web.exception.InputException;
import nextstep.jwp.web.network.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);

    private final RequestLine requestLine;
    private final Map<String, String> headers;
    private final String body;

    public HttpRequest(InputStream inputStream) {
        try {
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            this.requestLine = RequestLine.of(bufferedReader.readLine());
            this.headers = parseHeaders(bufferedReader);
            this.body = parseBody(bufferedReader, headers);
        } catch (IOException exception) {
            throw new InputException("Exception while reading http request");
        }
    }

    private static Map<String, String> parseHeaders(BufferedReader bufferedReader) throws IOException {
        final Map<String, String> headers = new HashMap<>();
        String line = bufferedReader.readLine();
        while(!"".equals(line)) {
            final String[] keyValue = line.split(":");
            headers.put(keyValue[0].trim(), keyValue[1].trim());
            line = bufferedReader.readLine();
            if (line == null) {
                break;
            }
        }
        return headers;
    }

    private static String parseBody(BufferedReader bufferedReader, Map<String, String> headers) throws IOException {
        if (bufferedReader.ready()) {
            final int contentLength = Integer.parseInt(headers.get("Content-Length"));
            final char[] buffer = new char[contentLength];
            bufferedReader.read(buffer, 0, contentLength);
            return new String(buffer);
        }
        return "";
    }

    public HttpMethod getHttpMethod() {
        return requestLine.getHttpMethod();
    }

    public URI getURI() {
        return requestLine.getURI();
    }

    public Map<String, String> getBody() {
        final Map<String, String> bodyAsMap = new HashMap<>();
        final String[] params = body.split("&");
        for (String param : params) {
            final String[] keyAndValue = param.split("=");
            bodyAsMap.put(keyAndValue[0], keyAndValue[1]);
        }
        return bodyAsMap;
    }

    public String getPath() {
        return getURI().getPath();
    }
}
