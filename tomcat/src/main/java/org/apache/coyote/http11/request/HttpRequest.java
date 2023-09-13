package org.apache.coyote.http11.request;

import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpException;
import org.apache.coyote.http11.HttpHeader;
import org.apache.coyote.http11.HttpMethod;

public class HttpRequest {

    private final HttpMethod httpMethod;
    private final String path;
    private final String version;
    private final String query;
    private final HttpHeader httpHeader;
    private final RequestBody requestBody;

    public HttpRequest(final HttpMethod httpMethod, final String path, final String version, final String query,
                       final HttpHeader httpHeader, final RequestBody requestBody) {
        this.httpMethod = httpMethod;
        this.path = path;
        this.version = version;
        this.query = query;
        this.httpHeader = httpHeader;
        this.requestBody = requestBody;
    }

    public boolean containJsessionId() {
        return httpHeader.containJsessionId();
    }

    public boolean notContainJsessionId() {
        return httpHeader.notContainJsessionId();
    }

    public String findJsessionId() {
        return httpHeader.findJsessionId();
    }

    public boolean isGet() {
        return httpMethod.equals(HttpMethod.GET);
    }

    public boolean isPost() {
        return httpMethod.equals(HttpMethod.POST);
    }

    public boolean isFile() {
        try {
            ContentType.findByPath(this.path);
        } catch (final HttpException exception) {
            return false;
        }
        return true;
    }

    public String findBodyParameter(final String key) {
        return requestBody.getParameter(key);
    }

    public String getPath() {
        return path;
    }

    public RequestBody getRequestBody() {
        return requestBody;
    }

    public String getVersion() {
        return version;
    }
}
