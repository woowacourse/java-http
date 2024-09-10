package support;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class BaseHttpTest {

    protected String resolve200Response(String extension, URL url) throws URISyntaxException, IOException {
        String file = Files.readString(Path.of(url.toURI()));
        return "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/" + extension + ";charset=utf-8 \r\n" +
                "Content-Length: " + file.getBytes().length + " \r\n" +
                "\r\n" +
                file;
    }

    protected String resolve302Response(String location) {
        return String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Location: " + location
        );
    }

    protected String resolveGetRequestByPath(String path) {
        return String.join("\r\n",
                "GET " + path + " HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
    }

}
