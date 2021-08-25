package nextstep.jwp.framework.infrastructure.adapter.get;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import nextstep.jwp.framework.infrastructure.http.request.HttpRequest;
import nextstep.jwp.framework.infrastructure.resolver.StaticFileResolver;
import nextstep.jwp.framework.infrastructure.http.response.HttpResponse;
import nextstep.jwp.framework.infrastructure.http.status.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PageGetRequestAdapter extends HttpGetRequestAdapter {

    private static final Logger log = LoggerFactory.getLogger(PageGetRequestAdapter.class);

    public PageGetRequestAdapter(
        Class<?> target,
        Method method,
        StaticFileResolver staticFileResolver
    ) {
        super(target, method, staticFileResolver);
    }

    @Override
    public HttpResponse doService(HttpRequest httpRequest) {
        try {
            method.setAccessible(true);
            Constructor<?> declaredConstructor = target.getConstructor();
            Object target = declaredConstructor.newInstance();
            String resourcePath = (String) method.invoke(target);
            HttpRequest parsedHttpRequest = HttpRequest.ofStaticFile(resourcePath);
            return staticFileResolver.render(parsedHttpRequest, HttpStatus.OK);
        } catch (InstantiationException | InvocationTargetException
            | NoSuchMethodException | IllegalAccessException e) {
            log.error("Method Invoke or Bean Instantiation Error", e);
            return staticFileResolver.renderDefaultViewByStatus(HttpStatus.INTERNAL_SEVER_ERROR);
        }
    }
}
