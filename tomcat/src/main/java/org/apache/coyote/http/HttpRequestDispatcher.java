package org.apache.coyote.http;

import com.techcourse.web.request.AppRequest;
import com.techcourse.web.router.AppRouter;
import com.techcourse.web.view.AppResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.coyote.http.adapter.RequestConverter;
import org.apache.coyote.http.adapter.ResponseConverter;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;

@NoArgsConstructor(access = AccessLevel.NONE)
public class HttpRequestDispatcher {

    private static final HttpRequestDispatcher INSTANCE = new HttpRequestDispatcher();

    private final RequestConverter requestConverter;
    private final ResponseConverter responseConverter;
    private final AppRouter appRouter;

    private HttpRequestDispatcher() {
        this.requestConverter = RequestConverter.getInstance();
        this.responseConverter = ResponseConverter.getInstance();
        this.appRouter = AppRouter.getInstance();
    }

    public static HttpRequestDispatcher getInstance() {
        return INSTANCE;
    }

    public HttpResponse execute(final HttpRequest request) throws Exception {
        final AppRequest appRequest = requestConverter.toControllerRequest(request);

        final AppResponse appResponse = appRouter.route(appRequest);

        return responseConverter.toHttpResponse(request, appResponse);
    }
}
