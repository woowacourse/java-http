package org.apache.coyote.http11.controller;

import static org.apache.coyote.http11.response.header.HttpStatusCode.FOUND;
import static org.apache.coyote.http11.response.header.HttpStatusCode.OK;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.header.ContentType;
import org.apache.coyote.http11.response.header.Location;
import org.apache.coyote.http11.util.FileReader;

public abstract class AbstractController implements Controller {

    private static final String NOT_FOUND_REDIRECT_LOCATION = "/404.html";

    @Override
    public final void service(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        if (httpRequest.hasHttpMethodOf(HttpMethod.GET)) {
            doGet(httpRequest, httpResponse);
            return;
        }
        doPost(httpRequest, httpResponse);
    }

    protected final void readFile(String path, HttpResponse httpResponse) throws IOException {
        FileReader fileReader = FileReader.getInstance();
        if (fileReader.isValidFile(path)) {
            readFileToResponse(path, httpResponse, fileReader);
            return;
        }
        httpResponse.setHttpStatusCode(FOUND);
        httpResponse.addHeader(new Location(NOT_FOUND_REDIRECT_LOCATION));
    }

    private void readFileToResponse(String path, HttpResponse httpResponse, FileReader fileReader) throws IOException {
        httpResponse.setHttpStatusCode(OK);
        String responseBody = fileReader.generate(path);
        httpResponse.setResponseBody(responseBody);
        String contentType = Files.probeContentType(Path.of(path));
        httpResponse.addHeader(ContentType.findByFilePath(contentType));
    }

    protected abstract void doGet(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException;

    protected abstract void doPost(HttpRequest httpRequest, HttpResponse httpResponse);
}
