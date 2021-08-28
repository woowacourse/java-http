package nextstep.jwp;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class ViewController implements Controller {
    @Override
    public void doProcess(HttpRequest request, HttpResponse response) throws IOException {
        String path = request.getPath();
        String contentType = "text/html;charset=utf-8";
        if (path.endsWith(".css")) {
            contentType = "text/css;charset=utf-8";
        }
        URL url = getClass().getClassLoader().getResource("static" + path);
        Path filePath = null;
        String responseBody = null;
        if (url != null) {
            File file = new File(url.getFile());
            if (file.exists()) {
                filePath = new File(url.getFile()).toPath();
                responseBody = new String(Files.readAllBytes(filePath));
                response.setStatus(200);
                response.addHeader("Content-Type", contentType);
                response.addHeader("Content-Length", String.valueOf(responseBody.getBytes().length));
                response.write(responseBody);
                response.flush();
            }
        }
    }
}
