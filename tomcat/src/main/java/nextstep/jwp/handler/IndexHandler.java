package nextstep.jwp.handler;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import nextstep.jwp.http.HttpBody;
import nextstep.jwp.http.HttpHeaders;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.HttpStatus;
import nextstep.jwp.http.HttpStatusLine;
import nextstep.jwp.http.HttpVersion;

public class IndexHandler implements RequestHandler {

    private static final String RESOURCE_PATH = "static/index.html";

    @Override
    public HttpResponse handle(HttpRequest request) throws IOException {
        HttpStatus httpStatus = HttpStatus.OK;
        HttpVersion httpVersion = request.getHttpVersion();
        HttpStatusLine httpStatusLine = new HttpStatusLine(httpVersion, httpStatus);
        URL url = getClass().getClassLoader().getResource(RESOURCE_PATH);
        HttpBody httpBody = HttpBody.from(new String(Files.readAllBytes(Path.of(url.getPath()))));
        HttpHeaders httpHeaders = HttpHeaders.createDefaultHeaders(request.getNativePath(), httpBody);

        return new HttpResponse(httpStatusLine, httpHeaders, httpBody);
    }

}
