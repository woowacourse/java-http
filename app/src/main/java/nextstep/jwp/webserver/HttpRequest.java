package nextstep.jwp.webserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HttpRequest {

    private HttpHeaders httpHeaders = new HttpHeaders();
    private HttpMethod httpMethod;
    private HttpCookie httpCookie = new HttpCookie();
    private QueryParams queryParams = new QueryParams();
    private FormBody body = new FormBody();
    private String uri;

    public HttpRequest(BufferedReader bufferedReader) {
        parseRequest(bufferedReader);
    }

    private void parseRequest(BufferedReader bufferedReader) {
        try {
            parseRequestLine(bufferedReader.readLine());
            initHeaders(bufferedReader);
            initBody(bufferedReader);
        } catch (IOException e) {
            throw new RuntimeException("잘못된 요청 형식");
        }
    }

    private void parseRequestLine(String requestLine) {
        String[] tokens = requestLine.split(" ");
        httpMethod = HttpMethod.from(tokens[0]);
        initUriAndQueryParams(tokens[1]);
    }

    private void initUriAndQueryParams(String uriPath) {
        int index = uriPath.indexOf("?");
        if (index == -1) {
            uri = uriPath;
            queryParams = new QueryParams();
            return;
        }
        uri = uriPath.substring(0, index);
        queryParams = new QueryParams(uriPath.substring(index + 1));
    }

    private void initHeaders(BufferedReader br) throws IOException {
        List<String> lines = new ArrayList<>();
        for (String line = br.readLine(); !"".equals(line); line = br.readLine()) {
            lines.add(line);
        }
        httpHeaders = new HttpHeaders(lines);
        initCookie();
    }

    private void initCookie() {
        if (httpHeaders.contains("Cookie")) {
            httpCookie = new HttpCookie(httpHeaders.get("Cookie"));
        }
    }

    private void initBody(BufferedReader bufferedReader) throws IOException {
        if (httpMethod == HttpMethod.POST && httpHeaders.contains("Content-Length")) {
            int contentLength = Integer.parseInt(httpHeaders.get("Content-Length"));
            char[] buffer = new char[contentLength];
            int read = bufferedReader.read(buffer, 0, contentLength);
            if (read == -1) {
                return;
            }
            this.body = new FormBody(new String(buffer));
        }
    }

    public String getUri() {
        return uri;
    }

    public String getHeader(String header) {
        return httpHeaders.get(header);
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getCookie(String cookie) {
        return httpCookie.get(cookie);
    }

    public boolean containsCookie(String cookie) {
        return Objects.nonNull(httpCookie.get(cookie));
    }

    public String getQueryParam(String param) {
        return queryParams.get(param);
    }

    public String getBody(String name) {
        String value = body.get(name);
        if (Objects.isNull(value)) {
            throw new RuntimeException("body 없음");
        }
        return value;
    }
}
