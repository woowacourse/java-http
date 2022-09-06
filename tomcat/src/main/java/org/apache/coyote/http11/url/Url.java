package org.apache.coyote.http11.url;

import static org.apache.coyote.http11.response.ContentType.TEXT_HTML;

import org.apache.coyote.http11.request.HttpHeaders;
import org.apache.coyote.http11.response.Http11Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Url {
    protected static final Logger log = LoggerFactory.getLogger(Register.class);

    private String path;
    private final String httpMethod;

    protected Url(String path, String httpMethod) {
        validatePath(path);
        this.path = path;
        this.httpMethod = httpMethod;
    }

    private void validatePath(String path) {
        int index = path.indexOf(".");
        if (index == -1) {
            this.path = path + "." + TEXT_HTML.getExtension();

        }
    }

    public abstract Http11Response getResponse(HttpHeaders httpHeaders);

    public abstract Http11Response postResponse(HttpHeaders httpHeaders, String requestBody);

    public String getPath() {
        return path;
    }

    public String getHttpMethod() {
        return httpMethod;
    }
}
