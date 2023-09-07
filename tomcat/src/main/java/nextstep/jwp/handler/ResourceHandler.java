package nextstep.jwp.handler;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import nextstep.jwp.exception.NotFoundException;
import nextstep.jwp.http.common.HttpBody;
import nextstep.jwp.http.common.HttpHeaders;
import nextstep.jwp.http.common.HttpStatus;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.HttpStatusLine;

public class ResourceHandler implements RequestHandler {

    private static final String RESOURCE_BASE_PATH = "static";
    private static final RequestHandler HANDLER = new ResourceHandler();

    private ResourceHandler() {
    }

    public static RequestHandler getInstance() {
        return HANDLER;
    }

    @Override
    public HttpResponse handle(HttpRequest request) throws IOException {
        HttpStatusLine httpStatusLine = new HttpStatusLine(request.getHttpVersion(), HttpStatus.OK);
        URL url = getClass().getClassLoader().getResource(RESOURCE_BASE_PATH + request.getNativePath());
        if (url == null) {
            throw new NotFoundException("해당 URL을 찾을 수 없습니다. 요청 URL : " + request.getNativePath());
        }
        HttpBody httpBody = HttpBody.from(new String(Files.readAllBytes(Path.of(url.getPath()))));
        HttpHeaders httpHeaders = HttpHeaders.createDefaultHeaders(request.getNativePath(), httpBody);

        return new HttpResponse(httpStatusLine, httpHeaders, httpBody);
    }

}
