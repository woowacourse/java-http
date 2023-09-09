package nextstep.jwp.config;

import nextstep.jwp.db.InMemorySession;
import nextstep.jwp.exception.UnauthorizedException;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.Response;
import org.apache.coyote.http11.util.Resource;

public class LoginFilter{

//
//    public void doFilter(Request request, Response response, FilterChain filterChain) {
//        final String uri = request.getUri();
//        final var cookie = request.getCookie();
//
//        if(uri.equals("/login") && cookie.containsKey("JSESSIONID")){
//            response = validKey(cookie.get("JSESSIONID"));
//            return;
//        }
//        if(filterChain!= null){
//            filterChain.doFilter(request,response);
//        }
//    }
//
//    private Response validKey(String jSessionId){
//        if(InMemorySession.isLogin(jSessionId)){
//            return Response.builder()
//                    .status(HttpStatus.FOUND)
//                    .contentType("html")
//                    .location("/index.html")
//                    .responseBody(Resource.getFile("index.html"))
//                    .build();
//        }
//        throw new UnauthorizedException("인가되지 않은 코드입니다.");
//    }
}
