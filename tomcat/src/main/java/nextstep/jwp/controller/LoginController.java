package nextstep.jwp.controller;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.db.InMemorySession;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UnauthorizedException;
import nextstep.jwp.model.AuthUser;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.Response;

public class LoginController {
    public static Response getLogin(Request request){
        Map<String, String> query = request.getQueries().orElseThrow();
        AuthUser authUser = AuthUser.from(query);
        User user = InMemoryUserRepository.findByAccount(authUser.getAccount()).orElseThrow(()->new UnauthorizedException("해당 유저가 없습니다."));
        if(!user.checkPassword(authUser.getPassword())){
            throw new UnauthorizedException("아이디 및 패스워드가 틀렸습니다.");
        }
        String jSessionId = InMemorySession.login(user);
        Map<String,String> cookie = new HashMap<>();
        if(!request.getCookie().containsKey("JSESSIONID")){
            cookie.put("JSESSIONID",jSessionId);
        }
        return Response.builder()
                .status(HttpStatus.FOUND)
                .contentType(request.getContentType())
                .responseBody(request.getResponseBody())
                .cookie(cookie)
                .location("index.html")
                .build();
    }

    public static Response signUp(Request request){
        Map<String, String> requestBody = request.getBody();
        final String account = requestBody.get("account");
        final String password = requestBody.get("password");
        final String email = requestBody.get("email");
        User user = new User(account,password,email);
        InMemoryUserRepository.save(user);
        return Response.builder()
                .status(HttpStatus.FOUND)
                .contentType(request.getContentType())
                .responseBody(request.getResponseBody())
                .location("index.html")
                .build();
    }
}
