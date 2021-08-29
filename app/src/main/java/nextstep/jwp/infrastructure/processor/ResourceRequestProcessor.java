package nextstep.jwp.infrastructure.processor;

import nextstep.jwp.model.ContentType;
import nextstep.jwp.model.CustomHttpRequest;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;

public class ResourceRequestProcessor implements RequestProcessor {

    private static final String DEFAULT_PATH = "static";

    @Override
    public String processResponse(CustomHttpRequest request, OutputStream outputStream) {
        String uri = request.getUri();
        String fileSource = fileSourceFromUri(uri);
        if (fileSource == null) {
            return String.join("\r\n",
                    "HTTP/1.1 404 NOT FOUND ",
                    "Location: http://localhost:8080/login.html ",
                    "",
                    "");
        }

        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + ContentType.contentTypeFromUri(uri) + ";charset=utf-8 ",
                "Content-Length: " + fileSource.getBytes().length + " ",
                "",
                fileSource);
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
