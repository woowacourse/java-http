package nextstep.jwp.presentation;

import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.constant.HttpMethod;

public abstract class AbstractController implements Controller {

    @Override
    public final void service(final HttpRequest request, final HttpResponse response) throws Exception {
        final HttpMethod method = request.getMethod();

        if (HttpMethod.GET == method) {
            doGet(request, response);
        } else if (HttpMethod.POST == method) {
            doPost(request, response);
        }
    }

    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
    }

    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
    }
}
