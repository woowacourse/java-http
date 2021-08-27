package nextstep.jwp.http;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ResourceResolver {
    private static final List<String> RESOURCE_SUFFIX = List.of(".html", ".js", ".css", ".csv");

    public static boolean checkIfUriHasResourceExtension(String uri) {
        for (String suffix : RESOURCE_SUFFIX) {
            if (uri.endsWith(suffix)) {
                return true;
            }
        }
        return false;
    }

    public static String resolveResourceRequest(HttpRequest request) throws IOException {
        try {
            final URL resource = ResourceResolver.class.getClassLoader().getResource("static" + request.uri());
            final Path path = new File(resource.getPath()).toPath();
            String responseBody = Files.readString(path);
            String contentType = Files.probeContentType(path);

            return String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + contentType + ";charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("존재하지 않는 Resource 요청입니다");
        }
    }
}
