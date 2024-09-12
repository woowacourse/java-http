package org.apache.coyote.request;

import java.util.Objects;
import java.util.UUID;
import org.apache.coyote.request.body.RequestBody;
import org.apache.coyote.request.requestLine.RequestLine;
import org.apache.coyote.request.header.RequestHeader;
import org.apache.coyote.session.Session;
import org.apache.coyote.util.ContentType;

public class HttpRequest {

    private final RequestLine requestLine;
    private final RequestHeader header;
    private final RequestBody requestBody;

    public HttpRequest(RequestLine requestLine, RequestHeader header, RequestBody requestBody) {
        this.requestLine = requestLine;
        this.header = header;
        this.requestBody = requestBody;
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public String getValueFromBody(String key) {
        return requestBody.getValue(key);
    }

    public String getResourcePath() {
        ContentType contentType = findContentType();

        return requestLine.getNoExtensionPath() + contentType.getExtension();
    }

    public Session getSession(boolean isNew) {
        if (isNew) {
            return new Session(String.valueOf(UUID.randomUUID()));
        }
        return header.findSession();
    }

    public boolean isDefaultRequestPath() {
        return requestLine.isDefaultPath();
    }

    public ContentType findContentType() {
        if (requestLine.isResourcePath()) {
            return ContentType.findContentTypeByPath(requestLine.getPathValue());
        }
        return header.findAcceptType();
    }

    public boolean hasCookie() {
        return header.hasCookie();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HttpRequest that = (HttpRequest) o;
        return Objects.equals(requestLine, that.requestLine) && Objects.equals(header, that.header)
                && Objects.equals(requestBody, that.requestBody);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestLine, header, requestBody);
    }
}
