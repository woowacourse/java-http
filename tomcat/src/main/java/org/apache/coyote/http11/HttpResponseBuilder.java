package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.exception.UncheckedServletException;

public class HttpResponseBuilder {

    private final String startLine;
    private final Map<String, String> headers;
    private final Map<String, String> cookies;
    private String body;

    HttpResponseBuilder(final String startLine) {
        this.startLine = startLine;
        this.headers = new HashMap<>();
        this.cookies = new HashMap<>();
        this.body = "";
    }

    public HttpResponseBuilder header(final String name, final String value) {
        headers.put(name, value);
        return this;
    }

    public HttpResponseBuilder fileBody(final String filePath) {
        try {
            final URL resource = getClass()
                    .getClassLoader()
                    .getResource("static" + filePath);
            body = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
            return this;
        } catch (IOException e) {
            throw new UncheckedServletException(e);
        }
    }

    public HttpResponseBuilder textBody(final String body) {
        this.body = body;
        return this;
    }

    public HttpResponse build() {
        HttpResponse response = new HttpResponse(startLine);
        for (String headerName : headers.keySet()) {
            response.addHeader(headerName, headers.get(headerName));
        }

        for (String cookieName : cookies.keySet()) {
            response.addCookie(cookieName, cookies.get(cookieName));
        }

        response.addBody(body);
        return response;
    }
}
