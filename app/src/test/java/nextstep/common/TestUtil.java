package nextstep.common;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import nextstep.jwp.framework.infrastructure.http.status.HttpStatus;

public class TestUtil {

    private static final String RESPONSE_FORMAT =
        String.join("\r\n",
            "HTTP/1.1 %s %s ",
            "Content-Type: text/%s;charset=utf-8 ",
            "Content-Length: %d ", ""
        );

    private TestUtil() {
    }

    public static String writeResponse(String url, HttpStatus httpStatus) {
        try {
            url = "/static" + url;
            Path path = Paths.get(TestUtil.class.getResource(url).toURI());
            String responseBody = String.join("\r\n", Files.readAllLines(path));
            String header = String.format(
                RESPONSE_FORMAT,
                httpStatus.getCode(),
                httpStatus.getMessage(),
                extractExtension(path),
                responseBody.getBytes().length
            );
            if (httpStatus == HttpStatus.FOUND) {
                header += "Location: /index.html"  + "\r\n";
            }
            return header + "\r\n" + responseBody;
        } catch (IOException | URISyntaxException exception) {
            throw new IllegalStateException(exception);
        }
    }

    private static String extractExtension(Path path) {
        String resourcePath = path.toString();
        int lastIndex = resourcePath.lastIndexOf(".");
        String extension = resourcePath.substring(lastIndex + 1);
        if (extension.equals("js")) {
            extension = "javascript";
        }
        return extension;
    }
}
