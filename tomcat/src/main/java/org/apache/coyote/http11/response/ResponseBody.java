package org.apache.coyote.http11.response;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.coyote.http11.request.RequestLine;

public class ResponseBody {

    private final String content;

    public ResponseBody(final String content) {
        this.content = content;
    }

    public static ResponseBody form(final RequestLine requestLine) throws URISyntaxException, IOException {
        final String resource = requestLine.getPath();
        if (resource.equals("/")) {
            return new ResponseBody("Hello world!");
        }
        final URL url = ResponseBody.class.getResource("/static" + makefileExtension(resource));
        if (url == null) {
            return new ResponseBody(null);
        }
        final Path path = Paths.get(url.toURI()).toFile().toPath();

        return new ResponseBody(Files.readString(path));
    }

    private static String makefileExtension(final String path) {
        if (path.equals("/login")) {
            return "/login.html";
        }
        if (path.equals("/register")) {
            return "/register.html";
        }
        return path;
    }

    public String getContent() {
        return content;
    }
}
