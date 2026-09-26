package org.apache.catalina;

import java.io.IOException;
import java.io.InputStream;
import org.apache.coyote.http11.HttpResponse;

public class StaticResourceResponder {

    public HttpResponse serve(String path) throws IOException {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("static" + path)) {
            if (inputStream == null) {
                HttpResponse response = new HttpResponse();
                response.notFound();
                return response;
            }

            byte[] resource = inputStream.readAllBytes();
            HttpResponse response = new HttpResponse();
            response.setBody(resource, getContentType(path));
            return response;
        }
    }

    private String getContentType(String path) {
        if (path.endsWith(".html")) {
            return "text/html;charset=utf-8";
        }
        if (path.endsWith(".css")) {
            return "text/css;charset=utf-8";
        }
        return "application/octet-stream";
    }
}
