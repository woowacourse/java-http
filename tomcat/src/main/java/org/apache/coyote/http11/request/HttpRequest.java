package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.apache.coyote.http11.common.Cookies;
import org.apache.coyote.http11.common.Headers;
import org.apache.coyote.http11.common.HttpProtocolVersion;

public class HttpRequest {

    private HttpMethod method;
    private Path path;
    private HttpProtocolVersion version;
    private Headers headers = new Headers();
    private Cookies cookies = new Cookies();
    private Parameters query = new Parameters();
    private Parameters body = new Parameters();

    private static final int MAX_REQUEST_SIZE = 104_857_600; // 10MB

    public HttpRequest(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        setHeadLine(reader);
        setCookieAndHeader(reader);
        setBody(reader);
    }

    private void setHeadLine(BufferedReader reader) throws IOException {
        String line = reader.readLine();
        if (line.split(" ").length < 3) {
            throw new IllegalArgumentException("유효하지 않은 요청 포맷입니다.");
        }
        String[] words = line.split(" ");
        path = new Path(words[1].split("\\?")[0]);
        method = HttpMethod.from(words[0]);
        version = HttpProtocolVersion.from(words[2]);
        setQueryParameter(words[1]);
    }

    private void setQueryParameter(String pathWithQuery) {
        if (pathWithQuery.split("\\?").length > 1) {
            String paramString = pathWithQuery.split("\\?")[1];
            for (var p : paramString.split("&")) {
                query.put(p);
            }
        }
    }

    private void setCookieAndHeader(BufferedReader reader) throws IOException {
        int byteSum = 0;
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.isEmpty()) {
                break;
            }
            byteSum += line.length() + 2;
            validateMaxSize(byteSum);
            String[] set = line.split(": ");
            if (set[0].equals("Cookie")) {
                cookies = Cookies.from(line);
            } else {
                headers.put(set[0], set[1]);
            }
        }
    }

    private static void validateMaxSize(int byteSum) {
        if (byteSum > MAX_REQUEST_SIZE) {
            throw new IllegalArgumentException("최대 크기를 초과한 요청입니다.");
        }
    }

    private void setBody(BufferedReader reader) throws IOException {
        int contentLength = headers.getContentLength();
        if (contentLength == 0) {
            body = new Parameters();
            return;
        }
        char[] buffer = new char[contentLength];
        int bytesRead = reader.read(buffer, 0, contentLength);
        body = parseParameters(new String(buffer, 0, bytesRead));
    }

    private Parameters parseParameters(String originalParams) {
        Parameters parameters = new Parameters();
        for (var p : originalParams.split("&")) {
            parameters.put(p);
        }
        return parameters;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public Path getPath() {
        return path;
    }

    public HttpProtocolVersion getVersion() {
        return version;
    }

    public Headers getHeaders() {
        return headers;
    }

    public Cookies getCookies() {
        return cookies;
    }

    public Parameters getQuery() {
        return query;
    }

    public Parameters getBody() {
        return body;
    }

    public void setPath(String path) {
        this.path = new Path(path);
    }

    public Api getApi() {
        return new Api(method, path.get());
    }
}
