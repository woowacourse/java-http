package org.apache.coyote.http11.url;

import static org.apache.coyote.http11.response.ContentType.TEXT_HTML;

import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.request.HttpHeaders;
import org.apache.coyote.http11.response.Http11Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Url {
    protected static final Logger log = LoggerFactory.getLogger(Register.class);

    protected String path;
    protected final Http11Request request;

    protected Url(String path, Http11Request request) {
        validatePath(path);
        this.path = path;
        this.request = request;
    }

    private void validatePath(String path) {
        int index = path.indexOf(".");
        if (index == -1) {
            this.path = path + "." + TEXT_HTML.getExtension();
        }
    }

    public abstract Http11Response handle(HttpHeaders httpHeaders, String requestBody);

    public String getPath() {
        return path;
    }

    public Http11Request getRequest() {
        return request;
    }
}
