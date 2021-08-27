package nextstep.jwp.http;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static nextstep.jwp.Header.CONTENT_LENGTH;
import static nextstep.jwp.Header.CONTENT_TYPE;

public class Response {

    public static byte[] ok(String resourcePath) throws IOException {
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put(CONTENT_TYPE, "text/html;charset=utf-8 ");
        return ok(headerMap, resourcePath);
    }

    public static byte[] ok(Map<String, String> header, String resourcePath) throws IOException {
        URL resource = Response.class.getClassLoader().getResource("static" + resourcePath);
        byte[] body = new byte[0];
        if (resource != null) {
            final Path path = new File(resource.getPath()).toPath();
            body = Files.readAllBytes(path);
        }

        header.put(CONTENT_LENGTH, String.valueOf(body.length));

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("HTTP/1.1 200 OK \r\n");

        for (Map.Entry<String, String> entry : header.entrySet()) {
            stringBuilder.append(entry.getKey() + ": " + entry.getValue() + " \r\n");
        }
        stringBuilder.append("\r\n");
        stringBuilder.append(new String(body) + "\r\n");

        return stringBuilder.toString().getBytes();
    }

    public static byte[] redirect302(String locationUrl) {
        return String.join("\r\n",
                "HTTP/1.1 302 Redirect ",
                "Location: " + locationUrl + " ",
                "").getBytes();
    }
}
