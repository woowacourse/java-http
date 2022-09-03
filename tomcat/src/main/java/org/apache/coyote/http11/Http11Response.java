package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import nextstep.jwp.exception.ResourcePathNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Response {

    private static final Logger log = LoggerFactory.getLogger(Http11Response.class);

    private final String contentType;
    private final String responseBody;

    public Http11Response(String contentType, String responseBody) {
        this.contentType = contentType;
        this.responseBody = responseBody;
    }

    public static Http11Response from(String resourcePath) {
        final URL resource = Thread.currentThread()
                .getContextClassLoader()
                .getResource("static" + resourcePath);
        validateResourcePath(resource);
        
        final String responseBody = getResponseBody(resource);
        final String contentType = resourcePath.split("\\.")[1];
        return new Http11Response(contentType, responseBody);
    }

    private static void validateResourcePath(URL resource) {
        if (resource == null) {
            throw new ResourcePathNotFoundException();
        }
    }

    private static String getResponseBody(URL resource) {
        try {
            return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new IllegalArgumentException("파일을 여는 동안 I/O 오류가 발생했습니다.");
        }
    }

    public String getOkResponse() {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                getContentTypeHeader(),
                getContentLengthHeader(),
                "",
                responseBody);
    }

    private String getContentTypeHeader() {
        return String.format("Content-Type: text/%s;charset=utf-8 ", contentType);
    }

    private String getContentLengthHeader() {
        return String.format("Content-Length: %s ", responseBody.getBytes().length);
    }


}
