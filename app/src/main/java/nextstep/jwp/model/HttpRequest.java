package nextstep.jwp.model;

import nextstep.jwp.util.HttpRequestUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HttpRequest {

    private String httpMethod;
    private String URL;
    private Map<String, String> headers = new HashMap<>();
    private Map<String, String> params = new HashMap<>();

    public HttpRequest(InputStream inputStream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        String line = br.readLine();

        if (Objects.isNull(line)) {
            return;
        }

        httpMethod = line.split(" ")[0];
        URL = line.split(" ")[1];

        insertHeaders(br, line);
        insertParams(br);
    }

    private void insertHeaders(BufferedReader br, String line) throws IOException {
        while (!Objects.equals(line, "")) {
            line = br.readLine();
            if (Objects.nonNull(line) && !line.isBlank()) {
                String[] split = line.split(": ");
                headers.put(split[0], split[1]);
            }
        }
    }

    private void insertParams(BufferedReader br) throws IOException {
        String contentLength = headers.get("Content-Length");
        if (Objects.isNull(contentLength)) {
            return;
        }

        int length = Integer.parseInt(contentLength);
        char[] buffer = new char[length];
        br.read(buffer, 0, length);
        String body = new String(buffer);
        this.params = HttpRequestUtils.parseQueryString(body);
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getURL() {
        return URL;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Map<String, String> getParams() {
        return params;
    }
}
