package org.apache.coyote.http11.response;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;
import org.apache.coyote.http11.request.HttpVersion;

public class HttpResponse implements SimpleServletResponse {

    private static final HttpVersion HTTP_VERSION_DEFAULT = HttpVersion.V1_1;
    private static final HttpStatusCode HTTP_STATUS_CODE_DEFAULT = HttpStatusCode.OK;

    private HttpVersion httpVersion = HTTP_VERSION_DEFAULT;
    private HttpStatusCode httpStatusCode = HTTP_STATUS_CODE_DEFAULT;
    private String location = "";
    private String cookie;

    private String contentType = "";
    private String charset = "utf-8";
    private int contentLength;
    private byte[] body = {};

    public HttpResponse() {
    }

    public void addToOutputStream(OutputStream outputStream) throws IOException {
        addRequestLine(outputStream);
        addHeaders(outputStream);
        addBody(outputStream);
    }

    private void addRequestLine(OutputStream outputStream) throws IOException {
        outputStream.write(String.join("\r\n",
                "HTTP/" + httpVersion.getVersion() + " " + httpStatusCode.toResponseFormat() + " ",
                "").getBytes());
    }

    private void addHeaders(OutputStream outputStream) throws IOException {
        if (httpStatusCode.isSuccess()) {
            outputStream.write(String.join("\r\n",
                    "Content-Type: " + contentType + ";charset=" + charset + " ",
                    "Content-Length: " + contentLength + " ",
                    "").getBytes());
        } else if (httpStatusCode.isRedirect()) {
            outputStream.write(String.join("\r\n",
                    "Location: http://localhost:8080" + location + " ",
                    "").getBytes());
        }
        if (Objects.nonNull(cookie) && !cookie.isEmpty()) {
            outputStream.write(String.join("\r\n",
                    "Set-Cookie: " + cookie + "; path =/" +" ",
                    "").getBytes());
        }
    }

    private void addBody(OutputStream outputStream) throws IOException {
        if (contentLength != 0) {
            outputStream.write("\r\n".getBytes());
            outputStream.write(body);
        }
    }

    public void setHttpVersion(HttpVersion httpVersion) {
        this.httpVersion = httpVersion;
    }

    public void setHttpStatusCode(HttpStatusCode httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public void setBody(byte[] body) {
        this.contentLength = body.length;
        this.body = body;
    }
}
