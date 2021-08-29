package nextstep.jwp.infrastructure.processor;

import nextstep.jwp.model.ContentType;
import nextstep.jwp.model.CustomHttpRequest;
import nextstep.jwp.model.CustomHttpResponse;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;

public class ResourceRequestProcessor implements RequestProcessor {

    private static final String DEFAULT_PATH = "static";
    private static final String DEFAULT_URI = "login.html";

    @Override
    public String processResponse(CustomHttpRequest request, OutputStream outputStream) {
        String uri = request.getUri();
        String fileSource = fileSourceFromUri(uri);
        if (fileSource == null) {
            return CustomHttpResponse.notFound(DEFAULT_URI);
        }

        return CustomHttpResponse.ok(ContentType.contentTypeFromUri(uri), fileSource);
    }

    private String fileSourceFromUri(String uri) {
        try {
            URL url = getClass().getClassLoader().getResource(DEFAULT_PATH + uri);
            File file = new File(url.getPath());
            return new String(Files.readAllBytes(file.toPath()));
        } catch (IOException | NullPointerException e) {
            return null;
        }
    }
}
