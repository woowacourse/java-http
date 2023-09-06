package nextstep.handler.util;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import nextstep.handler.util.exception.ResourceNotFoundException;
import org.apache.coyote.http.request.Request;
import org.apache.coyote.http.response.ContentType;
import org.apache.coyote.http.util.HttpHeaderConsts;

public class ResourceProcessor {

    public static String readResourceFile(final String resourceFullName) throws IOException {
        final URL resource = ClassLoader.getSystemClassLoader().getResource(resourceFullName);

        if (resource == null) {
            throw new ResourceNotFoundException();
        }

        final Path path = Paths.get(resource.getPath());
        return new String(Files.readAllBytes(path));
    }

    public static ContentType findContentType(final Request request, final String resourceFullName) {
        final String accept = request.findHeaderValue(HttpHeaderConsts.ACCEPT);

        if (accept == null) {
            return ContentType.findContentType(resourceFullName);
        }
        return ContentType.findContentType(accept, resourceFullName);
    }

    private ResourceProcessor() {
    }
}
