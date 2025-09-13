package org.apache.coyote.http11;

import java.io.IOException;
import java.io.InputStream;

public class StaticFileController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        handleStaticFileRequest(request.getPath(), response);
    }
    
    private void handleStaticFileRequest(String path, HttpResponse response) throws IOException {
        if (checkStaticFile(path, response)) {
            return;
        }
        response.sendError(HttpStatus.NOT_FOUND);
    }

    private boolean checkStaticFile(String path, HttpResponse response) throws IOException {
        if (path.equals("/")) {
            path = "/index.html";
        }

        InputStream in = getClass().getClassLoader().getResourceAsStream("static" + path);
        if (in == null && !path.contains(".")) {
            in = getClass().getClassLoader().getResourceAsStream("static" + path + ".html");
        }

        if (in == null) {
            return false;
        }

        serveStaticFile(response, in, path);
        return true;
    }

    private void serveStaticFile(HttpResponse response, InputStream inputStream, String path) throws IOException {
        try (inputStream) {
            String ext = "";
            if (path.contains(".")) {
                ext = path.substring(path.lastIndexOf(".") + 1);
            }

            String contentType = MimeTypeResolver.resolve(ext);
            byte[] body = inputStream.readAllBytes();

            response.send(HttpStatus.OK, contentType, body);
        }
    }
}
