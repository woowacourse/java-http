package nextstep.jwp.config;

import nextstep.jwp.db.InMemorySession;
import org.apache.coyote.http11.filter.Filter;
import org.apache.coyote.http11.filter.FilterChain;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.Response;
import org.apache.coyote.http11.util.Resource;

public class LoginFilter implements Filter {

    @Override
    public Response doFilter(Request request, FilterChain filterChain) {
        final String uri = request.getPath();
        final var cookie = request.getCookie();

        if (uri.equals("/login") && cookie.containsKey("JSESSIONID")) {
            return validKey(cookie.get("JSESSIONID"));
        }
        return filterChain.doFilter(request);
    }

    private Response validKey(String jSessionId) {
        if (InMemorySession.isLogin(jSessionId)) {
            return Response.builder()
                    .status(HttpStatus.FOUND)
                    .contentType("html")
                    .location("/index.html")
                    .responseBody(Resource.getFile("index.html"))
                    .filtered()
                    .build();
        }
        return Response.badResponse(HttpStatus.UNAUTHORIZED).redirect(Resource.getFile("401.html"), "401.html");
    }
}
