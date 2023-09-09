package nextstep.jwp.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.db.InMemorySession;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UnauthorizedException;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.Response;
import org.apache.coyote.http11.servlet.Servlet;

public class LoginController {

    public static Response login(Request request){
        Map<String, String> body = request.getBody();
        User user = InMemoryUserRepository.findByAccount(body.get("account"))
                .orElseThrow(()->new UnauthorizedException("해당 유저가 없습니다."));
        if(!user.checkPassword(body.get("password"))){
            throw new UnauthorizedException("아이디 및 패스워드가 틀렸습니다.");
        }
        String jSessionId = InMemorySession.login(user);
        Map<String,String> cookie = new HashMap<>();
        if(!request.getCookie().containsKey("JSESSIONID")){
            cookie.put("JSESSIONID",jSessionId);
        }
        return Response.builder()
                .status(HttpStatus.FOUND)
                .contentType("html")
                .cookie(cookie)
                .location("index.html")
                .responseBody(getFile("index.html"))
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
                .contentType("html")
                .location("index.html")
                .responseBody(getFile("index.html"))
                .build();
    }


    private static String getFile(String fileName){
        try {
            final var fileUrl = Servlet.class.getClassLoader().getResource("static/" + fileName);
            final var fileBytes = Files.readAllBytes(new File(fileUrl.getFile()).toPath());
            return new String(fileBytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NullPointerException e){
            return "";
        }
    }
}
