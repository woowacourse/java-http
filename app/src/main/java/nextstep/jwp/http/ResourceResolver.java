package nextstep.jwp.http;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import nextstep.jwp.exception.MethodNotAllowedException;
import nextstep.jwp.exception.NotFoundException;
import nextstep.jwp.http.entity.HttpStatus;
import nextstep.jwp.http.entity.HttpUri;

public class ResourceResolver {
    private static final String RESOURCE_PREFIX = "static";
    private static final List<String> RESOURCE_SUFFIXES = List.of(".html", ".js", ".css", ".csv", ".ico");

    private ResourceResolver() {
    }

    public static boolean checkIfUriHasResourceExtension(HttpUri uri) {
        for (String suffix : RESOURCE_SUFFIXES) {
            if (uri.endsWith(suffix)) {
                return true;
            }
        }
        return false;
    }

    public static void resolveResourceRequest(HttpRequest request, HttpResponse httpResponse) throws IOException {
        if (!request.method().isSame("GET")) {
            throw new MethodNotAllowedException("Resource요청은 GET만 가능합니다.");
        }

        final URL resource = ResourceResolver.class.getClassLoader()
                .getResource(RESOURCE_PREFIX + request.uri().path());
        if (Objects.isNull(resource)) {
            throw new NotFoundException("존재하지 않는 자원입니다.");
        }
        final Path path = new File(resource.getPath()).toPath();
        String responseBody = Files.readString(path);
        String contentType = Files.probeContentType(path);

        httpResponse.setHttpStatus(HttpStatus.OK);
        httpResponse.setHttpBody(contentType, responseBody);
    }
}
