package nextstep.jwp.ui.response;

import nextstep.jwp.ui.common.HttpCookie;
import nextstep.jwp.ui.common.HttpHeaders;
import nextstep.jwp.ui.common.ResourceFile;

import java.util.LinkedHashMap;
import java.util.Map;

public class HttpResponse {

    private String responseLine;
    private String response;
    private HttpHeaders headers;
    private HttpCookie httpCookie;

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
        response = String.join("\r\n",
                responseLine,
                "Location: " + url);
    }

    public void forward(String url, HttpStatus httpStatus) {
        ResourceFile resourceFile = new ResourceFile(url);
        String content = resourceFile.getContent();
        setStatus(httpStatus);
        if (resourceFile.getContentType().equals("text/html;charset=utf-8")) {
            addHeader("Set-Cookie", httpCookie.convertString());
        }
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
}
