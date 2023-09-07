package nextstep.jwp.handler;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import nextstep.jwp.http.common.HttpBody;
import nextstep.jwp.http.common.HttpHeaders;
import nextstep.jwp.http.common.HttpStatus;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.HttpStatusLine;

public class IndexHandler implements RequestHandler {

    private static final String RESOURCE_PATH = "static/index.html";

    @Override
    public HttpResponse handle(HttpRequest request) throws IOException {
        HttpStatusLine httpStatusLine = new HttpStatusLine(request.getHttpVersion(), HttpStatus.OK);
        URL url = getClass().getClassLoader().getResource(RESOURCE_PATH);
        HttpBody httpBody = HttpBody.from(new String(Files.readAllBytes(Path.of(url.getPath()))));
        HttpHeaders httpHeaders = HttpHeaders.createDefaultHeaders(request.getNativePath(), httpBody);

        return new HttpResponse(httpStatusLine, httpHeaders, httpBody);
    }

}
