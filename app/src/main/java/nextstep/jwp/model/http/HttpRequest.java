package nextstep.jwp.model.http;

import nextstep.jwp.util.HttpRequestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HttpRequest {
    private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);

    private final RequestLine requestLine;
    private final HttpRequestHeaders headers;
    private Map<String, String> params = new HashMap<>();

    public HttpRequest(InputStream inputStream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        String line = br.readLine();

        requestLine = new RequestLine(line);

        headers = new HttpRequestHeaders();
        while (!Objects.equals(line, "")) {
            line = br.readLine();
            if (Objects.nonNull(line) && !line.isBlank()) {
                log.debug("header : {}", line);
                String[] split = line.split(": ");
                headers.add(split[0].trim(), split[1].trim());
            }
        }

        if (getMethod().isPost()) {
            String contentLength = headers.get("Content-Length");
            if (Objects.isNull(contentLength)) {
                return;
            }

            int length = Integer.parseInt(contentLength);
            char[] buffer = new char[length];
            br.read(buffer, 0, length);
            String body = new String(buffer);
            this.params = HttpRequestUtils.parseQueryString(body);
            return;
        }
        params = requestLine.getParams();
    }

    public Map<String, String> getParams() {
        return params;
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public HTTPMethod getMethod() {
        return requestLine.getMethod();
    }

    public String getParameter(String param) {
        return getParams().get(param);
    }

    public HttpRequestHeaders getHeaders() {
        return headers;
    }
}
