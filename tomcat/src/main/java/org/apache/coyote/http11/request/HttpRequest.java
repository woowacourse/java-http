package org.apache.coyote.http11.request;

import org.apache.coyote.http11.HttpHeader;
import org.apache.coyote.http11.HttpMethod;

public class HttpRequest {

    private final HttpMethod httpMethod;
    private final String path;
    private final String version;
    private final String query;
    private final HttpHeader httpHeader;
    private final RequestBody requestBody;

    public HttpRequest(final HttpMethod httpMethod, final String path, final String version, final String query, final HttpHeader httpHeader, final RequestBody requestBody) {
        this.httpMethod = httpMethod;
        this.path = path;
        this.version = version;
        this.query = query;
        this.httpHeader = httpHeader;
        this.requestBody = requestBody;
    }

    public boolean notContainJsessionId() {
        return httpHeader.notContainJsessionId();
    }

    public String findJsessionId() {
        return httpHeader.findJsessionId();
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
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
