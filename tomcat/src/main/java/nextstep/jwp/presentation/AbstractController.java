package nextstep.jwp.presentation;

import static org.reflections.Reflections.log;

import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.constant.HttpMethod;

public abstract class AbstractController implements Controller {

    @Override
    public final void service(final HttpRequest request, final HttpResponse response) throws Exception {
        final HttpMethod method = request.getMethod();

        try{
            if (HttpMethod.GET == method) {
                doGet(request, response);
            }
        } catch (final UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    abstract void doGet(HttpRequest request, HttpResponse response) throws Exception;
}
