package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.request.RequestHeader;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponseBuilder;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.apache.coyote.http11.common.HttpStatus.OK;
import static org.apache.coyote.http11.common.HttpStatus.FOUND;

public class StaticFileHandler {

    private StaticFileHandler() {
    }

    public static HttpResponse handle(final String requestURI, RequestHeader requestHeader) {
        try {
            String responseBody = findResponseBody(requestURI);
            if (requestURI.endsWith("html")) {
                return new HttpResponseBuilder().init()
                        .httpStatus(OK)
                        .header("Content-Type: text/html;charset=utf-8 ")
                        .header("Content-Length: " + responseBody.getBytes().length + " ")
                        .body(responseBody)
                        .build();
            }
            return new HttpResponseBuilder().init()
                    .httpStatus(OK)
                    .header("Content-Type: " + requestHeader.getHeaderValue("Accept").split(",")[0] + ";charset=utf-8 ")
                    .header("Content-Length: " + responseBody.getBytes().length + " ")
                    .body(responseBody)
                    .build();
        } catch (IOException | NullPointerException e) {
            return new HttpResponseBuilder().init()
                    .httpStatus(FOUND)
                    .header("Location: /404.html ")
                    .build();
        }
    }

    private static String findResponseBody(final String requestURI) throws IOException {
        URL requestedFile = ClassLoader.getSystemClassLoader().getResource("static" + requestURI);
        try (BufferedReader br = new BufferedReader(new FileReader(requestedFile.getFile(), Charset.forName("UTF-8")))) {
            StringBuilder sb = new StringBuilder();
            String str;
            while ((str = br.readLine()) != null) {
                sb.append(str + "\n");
            }
            return sb.toString();
        }
    }
}
