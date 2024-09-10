package org.apache.coyote.http11.response;

import org.apache.coyote.file.FileExtension;
import org.apache.coyote.file.Resource;
import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.http11.header.Headers;
import org.apache.coyote.http11.header.ResponseHeader;
import org.apache.coyote.http11.path.Path;
import util.BiValue;
import util.StringUtil;

import static org.apache.coyote.http11.response.Charset.UTF_8;
import static org.apache.coyote.http11.response.ContentType.*;

public class HttpResponse {
    private HttpStatusCode statusCode;
    private final Headers headers;
    private String version;
    private byte[] body;

    public HttpResponse() {
        this.headers = new Headers();
        this.body = new byte[0];
    }

    public HttpResponse(final HttpStatusCode statusCode, final Headers headers, final String version, final byte[] body) {
        this.statusCode = statusCode;
        this.headers = headers;
        this.version = version;
        this.body = body;
    }

    public String getStatusLine() {
        return String.join(" ", version, statusCode.getCodeString(), statusCode.getReasonPhrase()) + " ";
    }

    public Headers getHeaders() {
        return headers;
    }

    public byte[] getBody() {
        return body;
    }

    public void addCookie(final String cookie, final String value) {
        headers.put(ResponseHeader.SET_COOKIE, cookie + "=" + value);
    }
    public void setRedirect(final Path path){
        setRedirect(path.value());
    }

    public void setRedirect(final String path) {
        headers.put(ResponseHeader.LOCATION, path);
        this.statusCode = HttpStatusCode.FOUND;
    }

    public void setStatus(final HttpStatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public void setResource(final Resource resource) {
        headers.put(ResponseHeader.CONTENT_TYPE, setContentType(resource.getExtension()));
        headers.put(ResponseHeader.CONTENT_LENGTH, String.valueOf(resource.length()));
        body = resource.getBytes();
    }

    public void setVersion(final String version) {
        this.version = version;
    }

    private static String setContentType(final FileExtension extension) {
        return switch (extension) {
            case CSS -> combineContentAndCharset(CSS, UTF_8);
            case HTML -> combineContentAndCharset(HTML, UTF_8);
            case JAVASCRIPT -> combineContentAndCharset(JAVASCRIPT, UTF_8);
            case UNKNOWN -> combineContentAndCharset(PLAIN, UTF_8);
        };
    }

    private static String combineContentAndCharset(final ContentType contentType, final Charset charset) {
        return StringUtil.combineWithDelimiter(new BiValue<>(contentType.getMimeType(), charset.getType()), ";");
    }
}
