package nextstep.jwp.webserver.controller;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

import nextstep.jwp.framework.context.AbstractController;
import nextstep.jwp.framework.http.HttpMethod;
import nextstep.jwp.framework.http.HttpRequest;
import nextstep.jwp.framework.http.HttpResponse;
import nextstep.jwp.framework.http.HttpStatus;
import nextstep.jwp.framework.http.template.ResourceResponseTemplate;
import nextstep.jwp.framework.util.ResourceUtils;

public class ResourceController extends AbstractController {

    private static final List<String> EXCEPTED_RESOURCE = new ArrayList<>();

    static {
        EXCEPTED_RESOURCE.add("/");
        EXCEPTED_RESOURCE.add("/favicon.ico");
    }

    public ResourceController() {
        super("/**", EnumSet.of(HttpMethod.GET));
    }

    @Override
    public boolean canHandle(HttpRequest httpRequest) {
        final String path = httpRequest.getPath();
        if (EXCEPTED_RESOURCE.contains(path)) {
            return false;
        }
        return ResourceUtils.exists(path);
    }

    @Override
    public HttpResponse doGet(HttpRequest httpRequest) {
        final HttpStatus httpStatus = HttpStatus.resolve(httpRequest.getPath());
        if (Objects.isNull(httpStatus)) {
            return new ResourceResponseTemplate().ok(httpRequest.getPath());
        }

        return new ResourceResponseTemplate().template(httpStatus, httpRequest.getPath());
    }
}
