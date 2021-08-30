package nextstep.jwp.web.handler;

import static nextstep.jwp.web.http.response.HttpStatus.INTERNAL_ERROR;
import static nextstep.jwp.web.http.response.HttpStatus.NOT_FOUND;
import static nextstep.jwp.web.http.response.HttpStatus.OK;

import java.util.List;
import nextstep.jwp.web.exception.NotFoundException;
import nextstep.jwp.web.http.MimeType;
import nextstep.jwp.web.http.request.HttpRequest;
import nextstep.jwp.web.http.response.HttpResponse;
import nextstep.jwp.web.http.response.body.HttpResponseBody;
import nextstep.jwp.web.resolver.DataResolver;
import nextstep.jwp.web.resolver.HtmlResolver;
import nextstep.jwp.web.resolver.StaticResourceResolver;

public class WebStatusHandler implements WebHandler {

    private static final String NOT_FOUND_PAGE_PATH = "/404";
    private static final String INTERNAL_ERROR_PAGE_PATH = "/500";

    private final List<DataResolver> dataResolvers;

    public WebStatusHandler() {
        this(List.of(new HtmlResolver(), new StaticResourceResolver()));
    }

    public WebStatusHandler(List<DataResolver> dataResolvers) {
        this.dataResolvers = dataResolvers;
    }

    @Override
    public void doHandle(HttpRequest request, HttpResponse response) {
        if (response.status() == OK) {
            response.replaceResponseBody(
                resolveData(request.methodUrl().url(), request.mimeType())
            );
        } else if (response.status() == NOT_FOUND) {
            response.replaceResponseBody(
                resolveData(NOT_FOUND_PAGE_PATH, MimeType.TEXT_HTML)
            );
        } else if (response.status() == INTERNAL_ERROR) {
            response.replaceResponseBody(
                resolveData(INTERNAL_ERROR_PAGE_PATH, MimeType.TEXT_HTML)
            );
        }
    }

    private HttpResponseBody resolveData(String filePath, MimeType acceptType) {
        for (DataResolver dataResolver : dataResolvers) {
            if (dataResolver.isSuitable(acceptType) &&
                dataResolver.isResourceExist(filePath)
            ) {
                return dataResolver.resolve(filePath);
            }
        }
        throw new NotFoundException(
            String.format("해당 타입의 파일이 없습니다. 요청받은 accept -> %s, filepath -> %s", acceptType,
                filePath));
    }
}
