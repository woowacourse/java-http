package nextstep.jwp.http;

import static nextstep.jwp.http.HttpResponse.ok;
import static nextstep.jwp.http.ViewResolver.resolveView;

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

            return ok(contentType, responseBody);
        } catch (NullPointerException e) {
            return resolveView("404");
        }
    }
}
