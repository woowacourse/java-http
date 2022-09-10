package org.apache.coyote.http11.response;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.apache.coyote.http11.FileType;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.ViewResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpResponse {

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String RESOURCES_PREFIX = "static";
    private static final Logger log = LoggerFactory.getLogger(HttpResponse.class);

    private DataOutputStream dos;
    private Map<String, String> headers = new LinkedHashMap<>();

    public HttpResponse(DataOutputStream dataOutputStream) {
        dos = dataOutputStream;
    }

    public void forward(String url) {
        try {
            String fileName = ViewResolver.convert(url);
            File file = getFile(fileName);
            FileType fileType = FileType.from(fileName);
            headers.put(CONTENT_TYPE, fileType.getMimeType());

            byte[] body = Files.readAllBytes(file.toPath());
            headers.put(CONTENT_LENGTH, body.length + "");

            response200Header(body.length);
            responseBody(body);
        } catch (IOException | URISyntaxException e) {
            log.error(e.getMessage());
        }
    }

    private void response200Header(int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            processHeaders();
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.writeBytes("\r\n");
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private File getFile(String fileName) throws URISyntaxException {
        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource(RESOURCES_PREFIX + fileName);

        return Paths.get(resource.toURI()).toFile();
    }

    public void sendRedirect(String redirectUrl) {
        try {
            dos.writeBytes("HTTP/1.1 302 FOUND \r\n");
            dos.writeBytes("Location: " + redirectUrl + " \r\n");
            processHeaders();
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void processHeaders() {
        try {
            Set<String> keys = headers.keySet();
            for (String key : keys) {
                dos.writeBytes(key + ": " + headers.get(key) + " \r\n");
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public void addCookie(HttpCookie cookie) {
        Map<String, String> cookies = cookie.getCookies();
        Set<String> keySet = cookies.keySet();
        for (String key : keySet) {
            String value = cookies.get(key);
            headers.put("Set-Cookie", key + "=" + value);
        }
    }
}
