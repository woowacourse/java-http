package org.apache.coyote.http11.controller.filecontroller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;
import org.apache.coyote.http11.controller.AbstractController;
import org.apache.coyote.http11.httpmessage.ContentType;
import org.apache.coyote.http11.httpmessage.request.HttpMethod;
import org.apache.coyote.http11.httpmessage.request.HttpRequest;
import org.apache.coyote.http11.httpmessage.response.HttpResponse;

public class FileController extends AbstractController {

    private static final Pattern FILE_URI_PATTERN = Pattern.compile("/.+\\.(html|css|js|ico)");

    @Override
    public boolean canHandle(HttpRequest httpRequest) {
        return httpRequest.matchRequestLine(HttpMethod.GET, FILE_URI_PATTERN);
    }

    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        URL resource = getClass().getClassLoader().getResource("static" + httpRequest.getPath());

        if (resource == null) {
            httpResponse.notFound();
            return;
        }

        File file = new File(resource.getFile());
        Path path = file.toPath();
        String responseBody = new String(Files.readAllBytes(path));
        ContentType contentType = ContentType.of(Files.probeContentType(path));

        httpResponse.ok(responseBody)
                .addHeader("Content-Type", contentType.getValue() + ";charset=utf-8 ");
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {

    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {

    }
}
