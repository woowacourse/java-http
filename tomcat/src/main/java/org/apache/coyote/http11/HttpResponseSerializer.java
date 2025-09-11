package org.apache.coyote.http11;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class HttpResponseSerializer {

    public static void write(HttpResponse httpResponse, OutputStream outputStream) throws IOException {
        outputStream.write(build(httpResponse).getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }

    private static String build(HttpResponse httpResponse) {
        StringBuilder builder = new StringBuilder();
        builder.append(httpResponse.getStatus().getValue()).append("\r\n");

        for (Map.Entry<String, String> entry : httpResponse.getHeaders().entrySet()) {
            builder.append(entry.getKey()).append(": ").append(entry.getValue()).append(" ").append("\r\n");
        }

        for (String cookie : httpResponse.getCookies()) {
            builder.append("Set-Cookie: ").append(cookie).append(" ").append("\r\n");
        }
        builder.append("\r\n");

        if (httpResponse.getBody() != null) {
            builder.append(httpResponse.getBody());
        }
        return builder.toString();
    }
}
