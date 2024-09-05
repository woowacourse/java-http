package org.apache.coyote;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class StaticResourceController extends AbstractController {

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        super.doPost(request, response);
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        String uri = request.getPath();
        System.out.println(uri);
        URL resource = getUrl(uri);
        String path = resource.getPath();
        String[] strings = path.split("\\.");
        String extension = strings[strings.length - 1];
        String file = new String(Files.readAllBytes(Path.of(resource.toURI())));
        response.setHeader("Content-Type", ContentType.getContentType(extension));
        response.setHeader("Content-Length", file.getBytes().length + " ");
        response.setBody(file);
    }

    private URL getUrl(String path) {
        path = "static" + path;
        return getClass().getClassLoader().getResource(path);
    }
}
