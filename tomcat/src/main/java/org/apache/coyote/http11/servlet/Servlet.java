package org.apache.coyote.http11.servlet;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.db.InMemorySession;
import nextstep.jwp.exception.UnauthorizedException;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.Response;

public class Servlet {

    private static List<String> api;
    static {
        api = List.of("/login","/register");
    }
    public static String getResponse(Request request){
        if(request.getUri().equals("/login")){
            var cookie = request.getCookie();
            if(cookie.containsKey("JSESSIONID")){
                if(InMemorySession.isLogin(cookie.get("JSESSIONID"))){
                    return getResponse(Request.ofStaticRequest("GET","/index.html"));
                }
            }
        }
        try {
            String uri = request.getUri();
            if (!uri.contains("?") && request.getBody().isEmpty()) {
                return Response.builder()
                        .status(HttpStatus.OK)
                        .contentType(request.getContentType())
                        .responseBody(request.getResponseBody())
                        .build().getResponse();
            }
            if (request.getApi().equals("/login") && request.getQueries().isPresent()) {
                return LoginController.getLogin(request).redirect(getFile("index.html"));
            }
            if(request.getApi().equals("/register")){
                return LoginController.signUp(request).redirect(getFile("index.html"));
            }
        } catch (UnauthorizedException unauthorizedException) {
            return Response.badResponse(HttpStatus.UNAUTHORIZED).redirect(getFile("401.html"));
        }
        return Response.badResponse(HttpStatus.NOTFOUND).redirect("500.html");
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
