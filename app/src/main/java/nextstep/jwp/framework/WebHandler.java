package nextstep.jwp.framework;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class WebHandler {

    private WebHandler() {
    }

    public static String run(final InputStream inputStream) throws IOException {
        final RequestHeader request = RequestHeader.from(inputStream);
        URL resource = request.getURL();

        assert resource != null;
        final Path path = new File(resource.getPath()).toPath();
        String content = Files.readString(path);

        return String.join("\r\n",
            "HTTP/1.1 200 OK ",
            "Content-Type: text/html;charset=utf-8 ",
            "Content-Length: " + content.getBytes().length + " ",
            "",
            content);
    }

}
