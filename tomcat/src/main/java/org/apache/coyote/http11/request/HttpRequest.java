package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import org.apache.coyote.http11.general.ContentType;
import org.apache.coyote.http11.general.HttpHeaders;
import org.apache.coyote.http11.request.spec.HttpMethod;
import org.apache.coyote.http11.request.spec.StartLine;
import org.apache.coyote.http11.session.Cookie;

public class HttpRequest {

    public static final String PARAM_DELIMITER = "&";
    public static final String KEY_VALUE_DELIMITER = "=";
    public static final int KEY_INDEX = 0;
    public static final int VALUE_INDEX = 1;

    private final StartLine startLine;
    private final HttpHeaders headers;
    private final String body;
    private final Map<String, String> parameters;

    public HttpRequest(StartLine startLine, HttpHeaders headers) {
        this(startLine, headers, null);
    }

    public HttpRequest(StartLine startLine, HttpHeaders headers, String body) {
        this.startLine = startLine;
        this.headers = headers;
        this.body = body;
        this.parameters = parseParameters(body);
    }

    private Map<String, String> parseParameters(String body) {
        if (headers.containsKey("ContentType") && headers.getContentType() == ContentType.APPLICATION_FORM_URLENCODED) {
            Map<String, String> params = new HashMap<>();
            String[] components = body.split(PARAM_DELIMITER);
            for (String component : components) {
                String[] keyVal = component.split(KEY_VALUE_DELIMITER);
                String key = keyVal[KEY_INDEX];
                String value = keyVal[VALUE_INDEX];
                params.put(key, value);
            }
        }
        return new HashMap<>();
    }

    @Nullable
    public String getParameter(String name) {
        if (!parameters.containsKey(name)) {
            return null;
        }
        return parameters.get(name);
    }

    public ContentType getContentType() {
        return headers.getContentType();
    }

    public boolean isPathEqualTo(String path) {
        return startLine.isPathEqualTo(path);
    }

    public boolean isStaticResourcePath() {
        return startLine.isStaticResourcePath();
    }

    public boolean hasCookie() {
        return headers.hasCookie();
    }

    public String getPathString() {
        return startLine.getPath();
    }

    public HttpMethod getMethod() {
        return startLine.getMethod();
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public Cookie getCookie() {
        return headers.getCookie();
    }
}
