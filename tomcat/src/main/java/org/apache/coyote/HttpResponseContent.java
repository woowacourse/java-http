package org.apache.coyote;

public class HttpResponseContent {

    private final String contentType;
    private final int contentLength;
    private final String body;

    public HttpResponseContent(String path, String body) {
        this.contentType = decideContentTypeByPath(path);
        this.contentLength = body.getBytes().length;
        this.body = body;
    }

    public String decideContentTypeByPath(String path) {
        if (path.endsWith(".html")) {
            return "text/html";
        }
        if (path.endsWith(".css")) {
            return "text/css";
        }
        if (path.endsWith(".js")) {
            return "application/javascript";
        }
        if (path.endsWith(".ico")) {
            return "image/x-icon";
        }
        throw new IllegalArgumentException("ContentType을 지원하지 않는 경로입니다: " + path);
    }

    public String getContentType() {
        return contentType;
    }

    public int getContentLength() {
        return contentLength;
    }

    public String getBody() {
        return body;
    }
}
