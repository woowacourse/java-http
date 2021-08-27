package nextstep.jwp.http.response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ResponseBody {

    private static final Logger log = LoggerFactory.getLogger(ResponseBody.class);

    private String body;

    public ResponseBody(String uri) {
        try {
            final StringBuilder stringBuilder = new StringBuilder();
            final URL resource = getClass().getClassLoader().getResource("static" + uri);
            assert resource != null;
            final Path resourcePath = new File(resource.getPath()).toPath();
            final List<String> requestBody = Files.readAllLines(resourcePath);

            for (String s : requestBody) {
                stringBuilder.append(s)
                        .append("\r\n");
            }
            this.body = stringBuilder.toString();
        } catch (Exception exception) {
            log.error("Exception set response body", exception);
        }
    }

    @Override
    public String toString() {
        return body;
    }
}
