package nextstep.jwp;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum GetRequestUri {
    INDEX("/") {
        @Override
        public String create() throws IOException {
            return GetRequestUri.createResponse(INDEX_WITH_FILE_NAME.httpRequestUri, "200 OK", "text/html");
        }
    },
    INDEX_WITH_FILE_NAME("/index.html") {
        @Override
        public String create() throws IOException {
            return GetRequestUri.createResponse(INDEX_WITH_FILE_NAME.httpRequestUri, "200 OK", "text/html");
        }
    },
    CSS("/css/styles.css") {
        @Override
        public String create() throws IOException {
            return GetRequestUri.createResponse(CSS.httpRequestUri, "200 OK", "text/css");
        }
    },
    LOGIN("/login") {
        @Override
        public String create() throws IOException {
            return GetRequestUri.createResponse(LOGIN.httpRequestUri + ".html", "200 OK", "text/html");
        }
    },
    REGISTER("/register") {
        @Override
        public String create() throws IOException {
            return GetRequestUri.createResponse(REGISTER.httpRequestUri + ".html", "200 OK", "text/html");
        }
    },
    NOT_FOUND("") {
        @Override
        public String create() throws IOException {
            return GetRequestUri.createResponse("/404.html", "404 Not Found", "text/html");
        }
    };

    private final String httpRequestUri;

    GetRequestUri(String httpRequestUri) {
        this.httpRequestUri = httpRequestUri;
    }

    public static String createResponse(String requestUri) throws IOException {
        if (requestUri.endsWith("js")) {
            return GetRequestUri.createResponse(requestUri, "200 OK", "text/javascript");
        }

        if (requestUri.endsWith("svg")) {
            return GetRequestUri.createResponse(requestUri, "200 OK", "image/svg+xml");
        }

        return Arrays.stream(GetRequestUri.values())
                     .filter(getRequestUri -> getRequestUri.hasSameUri(requestUri))
                     .findFirst()
                     .orElse(NOT_FOUND)
                     .create();
    }

    private static String createResponse(String requestUri, String statusCode, String contentType) throws IOException {
        final URL resource = GetRequestUri.class.getClassLoader().getResource("static" + requestUri);
        final Path path = new File(resource.getPath()).toPath();
        final List<String> lines = Files.readAllLines(path);

        String result = lines.stream()
                             .map(String::valueOf)
                             .collect(Collectors.joining());

        return String.join("\r\n",
                "HTTP/1.1 " + statusCode + " ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + result.getBytes().length + " ",
                "", result);
    }

    private boolean hasSameUri(String requestUri) {
        return httpRequestUri.equals(requestUri);
    }

    public abstract String create() throws IOException;
}
