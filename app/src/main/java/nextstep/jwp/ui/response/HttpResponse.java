package nextstep.jwp.ui.response;

import nextstep.jwp.ui.common.HttpHeaders;
import nextstep.jwp.ui.common.ResourceFile;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpResponse {

    private String responseLine;
    private String response;
    private HttpHeaders headers;

    public HttpResponse() {
        this.headers = new HttpHeaders(new LinkedHashMap<>());
    }

    public void setStatus(int code) {
        responseLine = "HTTP/1.1 " + HttpStatus.convert(code) + " ";
    }

    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    public void write(String message) {
        if (response != null) {
            response += message;
            return;
        }
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

    public HttpResponse sendRedirect(String url) {
        return sendRedirect(url, 302);
    }

    public HttpResponse sendRedirect(String url, int code) {
        setStatus(code);
        response = String.join("\r\n",
                responseLine,
                "Location: " + url);
        return this;
    }

    public HttpResponse forward(String url, int code) throws IOException {
        ResourceFile resourceFile = new ResourceFile(url);
        String content = resourceFile.getContent();
        setStatus(code);
        addHeader("Content-Type", resourceFile.getContentType());
        addHeader("Content-Length", String.valueOf(content.getBytes().length));
        write(content);
        return this;
    }

    public HttpResponse forward(String url) throws IOException {
        return forward(url, 200);
    }
}
