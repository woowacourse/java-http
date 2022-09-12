package nextstep.jwp.controller;

import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.session.SessionManager;

public abstract class AbstractController implements Controller {

    protected static final String INDEX_REDIRECT_PAGE = "/index.html";
    protected static final String ERROR_REDIRECT_PAGE = "/401.html";
    protected static final String EMPTY_VALUE = "";
    protected SessionManager sessionManager = Http11Processor.getSessionManager();

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        HttpMethod httpMethod = request.getHttpMethod();
        if (HttpMethod.GET.equals(httpMethod)) {
            doGet(request, response);
        }
        if (HttpMethod.POST.equals(httpMethod)) {
            doPost(request, response);
        }
    }

    protected abstract void doPost(HttpRequest request, HttpResponse response);

    protected abstract void doGet(HttpRequest request, HttpResponse response);
}
