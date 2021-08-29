package nextstep.jwp.framework.context;

import java.util.EnumSet;

import nextstep.jwp.framework.http.HttpMethod;
import nextstep.jwp.framework.http.HttpRequest;
import nextstep.jwp.framework.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractController implements Controller {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractController.class);

    protected final String mappingUri;

    protected final EnumSet<HttpMethod> handlingMethod;

    public AbstractController(String mappingUri, EnumSet<HttpMethod> handlingMethod) {
        this.mappingUri = mappingUri;
        this.handlingMethod = handlingMethod;
    }

    @Override
    public boolean canHandle(HttpRequest httpRequest) {
        return httpRequest.isSamePath(mappingUri) && handlingMethod.contains(httpRequest.getMethod());
    }

    @Override
    public final HttpResponse handle(HttpRequest httpRequest) {
        final HttpMethod httpMethod = httpRequest.getMethod();
        if (HttpMethod.GET.equals(httpMethod)) {
            return doGet(httpRequest);
        }

        if (HttpMethod.POST.equals(httpMethod)) {
            return doPost(httpRequest);
        }

        if (HttpMethod.PUT.equals(httpMethod)) {
            return doPut(httpRequest);
        }

        if (HttpMethod.DELETE.equals(httpMethod)) {
            return doDelete(httpRequest);
        }

        throw new IllegalArgumentException("등록되지 않은 HTTP Method 입니다.");
    }

    @Override
    public HttpResponse doGet(HttpRequest httpRequest) {

        LOGGER.error("{} {} 는 지원하지 않는 HTTP 메소드입니다.", httpRequest.getMethod(), httpRequest.getPath());

        throw new UnsupportedOperationException();
    }

    @Override
    public HttpResponse doPost(HttpRequest httpRequest) {

        LOGGER.error("{} {} 는 지원하지 않는 HTTP 메소드입니다.", httpRequest.getMethod(), httpRequest.getPath());

        throw new UnsupportedOperationException();
    }

    @Override
    public HttpResponse doPut(HttpRequest httpRequest) {

        LOGGER.error("{} {} 는 지원하지 않는 HTTP 메소드입니다.", httpRequest.getMethod(), httpRequest.getPath());

        throw new UnsupportedOperationException();
    }

    @Override
    public HttpResponse doDelete(HttpRequest httpRequest) {

        LOGGER.error("{} {} 는 지원하지 않는 HTTP 메소드입니다.", httpRequest.getMethod(), httpRequest.getPath());

        throw new UnsupportedOperationException();
    }
}
