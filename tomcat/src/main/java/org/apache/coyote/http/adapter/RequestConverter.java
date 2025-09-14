package org.apache.coyote.http.adapter;

import static common.session.SessionManager.JSESSIONID;

import com.techcourse.web.request.AppRequest;
import common.session.Session;
import common.session.SessionManager;
import java.util.Map;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.coyote.http.request.HttpRequest;

@NoArgsConstructor(access = AccessLevel.NONE)
public class RequestConverter {

    private static final RequestConverter INSTANCE = new RequestConverter();

    public static RequestConverter getInstance() {
        return INSTANCE;
    }

    public AppRequest toControllerRequest(final HttpRequest httpRequest) {
        final Map<String, String> queryParams = httpRequest.getRequestLine().getQueryParams();
        final Map<String, String> bodyParams = httpRequest.getBody().getParams();
        final Map<String, String> headers = httpRequest.getHeaders();

        final Session session = SessionManager.getInstance().find(httpRequest.getCookie(JSESSIONID))
                .orElse(new Session());

        return AppRequest.of(
                httpRequest.getMethod(),
                httpRequest.getPath(),
                queryParams,
                bodyParams,
                headers,
                session
        );
    }
}
