package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.exception.UncheckedServletException;

public class HttpResponseBuilder {

    private String startLine;
    private Map<String, String> headers;
    private String body;

    HttpResponseBuilder(String startLine) {
        this.startLine = startLine;
        this.headers = new HashMap<>();
        this.body = "";
    }

    public HttpResponseBuilder header(String name, String value) {
        headers.put(name, value);
        return this;
    }

    public HttpResponseBuilder fileBody(String filePath) {
        try {
            final URL resource = getClass().getClassLoader().getResource("static" + filePath);
            body =  new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
            return this;
        } catch (IOException e) {
            throw new UncheckedServletException(e);
        }
    }

    public HttpResponseBuilder textBody(String body) {
        this.body = body;
        return this;
    }

    public HttpResponse build() {
        HttpResponse response = new HttpResponse(startLine);
        for (String headerName : headers.keySet()) {
            response.addHeader(headerName, headers.get(headerName));
        }
        response.addBody(body);
        return response;
    }
}
