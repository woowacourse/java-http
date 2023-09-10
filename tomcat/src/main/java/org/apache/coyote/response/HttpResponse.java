package org.apache.coyote.response;

import org.apache.coyote.common.ContentType;
import org.apache.coyote.common.HttpCookie;

import java.util.LinkedHashMap;
import java.util.Map;

public class HttpResponse {

    private static final String DEFAULT_VERSION = "HTTP/1.1";

    private final StatusLine statusLine;
    private final HttpResponseHeader headers;

    private String body;

    private HttpResponse(StatusLine statusLine, HttpResponseHeader headers, String body) {
        this.statusLine = statusLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpResponse createDefaultResponse() {
        String version = DEFAULT_VERSION;
        HttpStatus httpStatus = HttpStatus.OK;
        Map<String, String> headers = new LinkedHashMap<>();
        String body = "";

        return new HttpResponse(new StatusLine(version, httpStatus), new HttpResponseHeader(headers), body);
    }

    private HttpResponse(Builder builder) {
        this.statusLine = new StatusLine(builder.version, builder.httpStatus);
        this.headers = new HttpResponseHeader(builder.headers);
        this.body = builder.body;
    }


    public String getResponse() {
        return statusLine.getStatusLine() + "\r\n" + headers.getResponseHeader() + "\r\n" + body;
    }

    public void setContentType(ContentType contentType) {
        this.headers.add("Content-Type", contentType.getType() + ";charset=utf-8");
    }

    public void setBody(String body) {
        this.body = body;
    }

    public static class Builder {
        private final String version;
        private final Map<String, String> headers;
        private HttpStatus httpStatus;
        private String body;

        public Builder() {
            this.version = DEFAULT_VERSION;
            this.httpStatus = HttpStatus.OK;
            this.headers = new LinkedHashMap<>();
            this.body = "";
        }

        public Builder header(String key, String value) {
            this.headers.put(key, value);
            return this;
        }

        public Builder status(HttpStatus httpStatus) {
            this.httpStatus = httpStatus;
            return this;
        }

        public Builder contentType(ContentType contentType) {
            this.headers.put("Content-Type", contentType.getType() + ";charset=utf-8");
            return this;
        }

        public Builder setCookie(HttpCookie cookie) {
            this.headers.put("Set-Cookie", cookie.convertToHeader());
            return this;
        }

        public Builder body(String body) {
            this.headers.put("Content-Length", String.valueOf(body.getBytes().length));
            this.body = body;
            return this;
        }

        public HttpResponse build() {
            return new HttpResponse(this);
        }
    }
}
