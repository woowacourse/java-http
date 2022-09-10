package nextstep.jwp.presentation;

import nextstep.jwp.exception.ResourceNotFoundException;
import nextstep.jwp.exception.UnauthorizedException;
import org.apache.coyote.Controller;
import org.apache.coyote.http11.common.HttpRequest;
import org.apache.coyote.http11.common.HttpResponse;
import org.apache.coyote.http11.constant.HttpMethod;
import org.apache.coyote.http11.constant.HttpStatus;
import org.apache.coyote.http11.util.StaticResource;

public abstract class AbstractController implements Controller {

    @Override
    public final void service(final HttpRequest request, final HttpResponse response) throws Exception {
        final HttpMethod method = request.getMethod();

        try {
            if (HttpMethod.GET == method) {
                doGet(request, response);
            } else if (HttpMethod.POST == method) {
                doPost(request, response);
            } else {
                throw new IllegalStateException("처리하지 않는 HTTP Method 요청입니다.");
            }
        } catch (final ResourceNotFoundException notFoundException) {
            response.setBody(StaticResource.notFound());
            response.setStatus(HttpStatus.NOT_FOUND);
        } catch (final UnauthorizedException unauthorizedException) {
            response.setBody(StaticResource.unauthorized());
            response.setStatus(HttpStatus.UNAUTHORIZED);
        } catch (final Exception exception) {
            response.setBody(StaticResource.internalServerError());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
    }

    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
    }
}
