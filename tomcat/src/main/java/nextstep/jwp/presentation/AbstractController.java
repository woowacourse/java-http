package nextstep.jwp.presentation;

import nextstep.jwp.exception.ResourceNotFoundException;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.constant.HttpMethod;
import org.apache.coyote.constant.HttpStatus;

public abstract class AbstractController implements Controller {

    @Override
    public final void service(final HttpRequest request, final HttpResponse response) throws Exception {
        final HttpMethod method = request.getMethod();

        try {
            if (HttpMethod.GET == method) {
                doGet(request, response);
            } else if (HttpMethod.POST == method) {
                doPost(request, response);
            }
        } catch (final ResourceNotFoundException notFoundException) {
            response.setBody(StaticResource.notFound());
            response.setStatus(HttpStatus.NOT_FOUND);
        }
    }

    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
    }

    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
    }
}
