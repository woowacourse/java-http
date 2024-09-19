package org.apache.coyote.http11;

import static org.apache.coyote.http11.Status.FOUND;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

public class HttpResponse {

    public static final String SET_COOKIE_PREFIX = "Set-Cookie";
    public static final String SESSION_ID_NAME = "JSESSIONID";
    public static final String KEY_VALUE_SEPARATOR = "=";

    private String body = "";
    private ResponseHeader responseHeader;

    public HttpResponse() {
    }

    private void generateResponseBody(String path, Status status) throws IOException {
        if (status == FOUND) {
            return;
        }
        if (path.equals("/")) {
            body = "Hello world!";
            return;
        }
        generateStaticResponseBody(path);
    }

    private void generateStaticResponseBody(String path) throws IOException {
        if (!path.contains(".")) {
            final URL resource = getClass().getClassLoader().getResource(path + ".html");
            body = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
            return;
        }
        final URL resource = getClass().getClassLoader().getResource(path);
        body = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
    }

    public void generateResponse(String path, Status status) throws IOException {
        generateResponseBody(path, status);
        responseHeader = new ResponseHeader(path, status, body.getBytes().length);
    }

    public void addHeader(String key, String value) {
        responseHeader.addHeader(key, value);
    }

    public String getResponse() {
        String header = responseHeader.getHeader();
        StringBuilder stringBuilder = new StringBuilder(header);
        stringBuilder.append("\r\n")
                .append(body);
        return stringBuilder.toString();
    }
}
