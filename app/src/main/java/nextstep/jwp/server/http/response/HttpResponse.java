package nextstep.jwp.server.http.response;

import nextstep.jwp.server.http.common.HttpCookie;
import nextstep.jwp.server.http.common.HttpHeaders;
import nextstep.jwp.server.http.common.ResourceFile;

import java.util.LinkedHashMap;
import java.util.Map;

public class HttpResponse {

    private String responseLine;
    private String response;
    private final HttpHeaders headers;
    private final HttpCookie httpCookie;

    public HttpResponse() {
        this.headers = new HttpHeaders(new LinkedHashMap<>());
        this.httpCookie = new HttpCookie();
    }

    public void setStatus(HttpStatus httpStatus) {
        responseLine = "HTTP/1.1 " + HttpStatus.convert(httpStatus) + " ";
    }

    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    public void write(String message) {
        response = String.join("\r\n",
                responseLine,
                headers.convertToLines(),
                "",
                message);
    }

    public String getResponseLine() {
        return responseLine;
    }

    public String getResponse() {
        return response;
    }

    public Map<String, String> getHeaders() {
        return headers.getHeaders();
    }

    public void sendRedirect(String url) {
        sendRedirect(url, HttpStatus.FOUND);
    }

    public void sendRedirect(String url, HttpStatus httpStatus) {
        setStatus(httpStatus);
        if (getHeaders().containsKey("Set-Cookie")) {
            response = String.join("\r\n",
                    responseLine,
                    "Location: " + url,
                    headers.convertToLines());
            return;
        }
        response = String.join("\r\n",
                responseLine,
                "Location: " + url);
    }

    public void forward(String url, HttpStatus httpStatus) {
        ResourceFile resourceFile = new ResourceFile(url);
        String content = resourceFile.getContent();
        setStatus(httpStatus);
        addHeader("Content-Type", resourceFile.getContentType());
        addHeader("Content-Length", String.valueOf(content.getBytes().length));
        write(content);
    }

    public void forward(String url) {
        forward(url, HttpStatus.OK);
    }

    public void addCookie(String name, String value) {
        httpCookie.addCookie(name, value);
    }

    public String convertCookieToString() {
        return httpCookie.convertString();
    }
}
