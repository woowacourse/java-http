package org.apache.view;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import org.apache.http.HttpResponse;

public class ViewUtils {

    public static HttpResponse render(HttpResponse httpResponse, String filePath)
            throws IOException, URISyntaxException {
        final ClassLoader classLoader = ViewUtils.class.getClassLoader();
        final URL url = classLoader.getResource("static" + filePath);

        if (url == null) {
            return HttpResponse.notFound();
        }

        final File resourceFile = new File(Objects.requireNonNull(url).toURI());
        final Path path = resourceFile.toPath();

        httpResponse.setResponseBody(new String(Files.readAllBytes(path)));

        return httpResponse;
    }
}
