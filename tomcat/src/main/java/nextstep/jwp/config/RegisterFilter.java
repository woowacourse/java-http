package nextstep.jwp.config;

import java.util.Map;
import nextstep.jwp.db.InMemorySession;
import org.apache.coyote.http11.filter.Filter;
import org.apache.coyote.http11.filter.FilterChain;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.Response;
import org.apache.coyote.http11.util.Resource;

public class RegisterFilter implements Filter {

    @Override
    public void doFilter(Request request, Response response, FilterChain filterChain) {

        final String uri = request.getPath();
        final Map<String, String> cookie = request.getCookie();

        if (( uri.equals("/register")) && cookie.containsKey("JSESSIONID")) {
            validKey(cookie.get("JSESSIONID"), response);
        }
        filterChain.doFilter(request, response);
    }

    private void validKey(String jSessionId, Response response) {
        if (InMemorySession.isLogin(jSessionId)) {
            response
                    .setStatus(HttpStatus.FOUND)
                    .setContentType("html")
                    .setLocation("/index.html")
                    .setResponseBody(Resource.getFile("index.html"))
                    .setFiltered(true);
            return;
        }
        response
                .setStatus(HttpStatus.UNAUTHORIZED)
                .setContentType("html")
                .setResponseBody(Resource.getFile("401.html"))
                .setLocation("/401.html")
                .setFiltered(true);
    }
}
