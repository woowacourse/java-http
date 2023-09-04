package org.apache.coyote.http11.handler;

import nextstep.jwp.db.InMemorySession;
import nextstep.jwp.exception.UnauthorizedException;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.Response;

public class LoginHandler extends Handler{
    private Handler next;

    @Override
    public void setNext(Handler handler) {
        this.next = handler;
    }

    @Override
    public String getResponse(Request request) {
        final String uri = request.getUri();
        final var cookie = request.getCookie();
        if(uri.equals("/login") && cookie.containsKey("JSESSIONID")){
            String jSessionId = cookie.get("JSESSIONID");
            return validKey(jSessionId);
        }
        return next.getResponse(request);
    }

    private String validKey(String jSessionId){
        if(InMemorySession.isLogin(jSessionId)){
            return Response.builder()
                    .status(HttpStatus.OK)
                    .contentType("html")
                    .responseBody(getFile("index.html"))
                    .build().getResponse();
        }
        throw new UnauthorizedException("인가되지 않은 코드입니다.");
    }
}
