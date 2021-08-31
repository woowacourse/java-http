package nextstep.jwp.controller;

import nextstep.jwp.http.*;

public class StaticResourceController extends Controller {
    @Override
    public boolean canHandle(final HttpRequest httpRequest) {
        final String httpMethod = httpRequest.getHttpMethod();
        final String path = httpRequest.getPath();
        return HttpMethod.isGet(httpMethod) && isStaticFileRequest(path);
    }

    private boolean isStaticFileRequest(String path) {
        return path.endsWith(".html")
                || path.endsWith(".css")
                || path.endsWith(".js")
                || path.endsWith(".ico");
    }

    @Override
    public HttpResponse doGet(final HttpRequest httpRequest) {
        String path = httpRequest.getPath();
        final String responseBody = readFile(path);
        final HttpStatus httpStatus = HttpStatus.findHttpStatusByUrl(path);

        return new HttpResponse(
                httpRequest.getProtocol(),
                httpStatus,
                ContentType.findByUrl(path),
                responseBody.getBytes().length,
                responseBody);
    }

    @Override
    public HttpResponse doPost(final HttpRequest httpRequest) {
        throw new UnsupportedOperationException();
    }
}
