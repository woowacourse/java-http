package nextstep.jwp.presentation;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import static org.apache.coyote.http11.Method.*;

public abstract class AbstractController implements Controller {

    @Override
    public HttpResponse service(HttpRequest httpRequest, HttpResponse httpResponse) {

        if (GET.matches(httpRequest.getMethod())) {
            return doGet(httpRequest, httpResponse);
        }
        if (POST.matches(httpRequest.getMethod())) {
            return doPost(httpRequest, httpResponse);
        }
        throw new IllegalArgumentException("지원하지 않는 HTTP Method 입니다.");
    }

    protected abstract HttpResponse doGet(HttpRequest httpRequest, HttpResponse httpResponse);

    protected abstract HttpResponse doPost(HttpRequest httpRequest, HttpResponse httpResponse);
}
