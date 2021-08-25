package nextstep.jwp.framework.infrastructure.adapter.post;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import nextstep.jwp.framework.infrastructure.http.request.HttpRequest;
import nextstep.jwp.framework.infrastructure.resolver.StaticFileResolver;
import nextstep.jwp.framework.infrastructure.http.response.HttpResponse;
import nextstep.jwp.framework.infrastructure.http.status.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterRequestAdapter extends HttpPostRequestAdapter {

    private static final Logger log = LoggerFactory.getLogger(RegisterRequestAdapter.class);

    public RegisterRequestAdapter(
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
            Map<String, String> attributes = httpRequest.getContentAsAttributes();
            String account = attributes.get("account");
            String password = attributes.get("password");
            String email = attributes.get("email");
            method.invoke(target, account, password, email);
            HttpRequest redirectRequest = HttpRequest.ofStaticFile("/index.html");
            return staticFileResolver.render(redirectRequest, HttpStatus.FOUND);
        } catch (InstantiationException | InvocationTargetException
            | NoSuchMethodException | IllegalAccessException e) {
            log.error("Method Invoke or Bean Instantiation Error", e);
            return staticFileResolver.renderDefaultViewByStatus(HttpStatus.UNAUTHORIZED);
        }
    }
}
