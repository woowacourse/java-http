package org.apache.coyote.http11.servlet;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.exception.UnauthorizedException;
import org.apache.coyote.http11.exception.NoSuchApiException;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.Response;

public class Servlet {
    private static List<String> api;
    static {
        api = List.of("/login","/register");
    }
    public static String getResponse(Request request){
        try {
            String uri = request.getUri();

            if (!uri.contains("?") && request.getBody().isEmpty()) {
                return getStaticResponse(request).getRequest();
            }
            if (request.getApi().equals("/login") && request.getQueries().isPresent()) {
                return LoginController.getLogin(request).redirect("index.html",getFile("index.html"));
            }
            if(request.getApi().equals("/register")){
                return LoginController.signUp(request).redirect("index.html",getFile("index.html"));
            }
        } catch (UnauthorizedException unauthorizedException) {
            return Response.of(HttpStatus.UNAUTHORIZED,"html",getFile("401.html")).redirect("401.html",getFile("401.html"));
        }
        throw new NoSuchApiException("해당 api가 없습니다.");
    }
    private static Response getStaticResponse(Request request){
        return Response.of(HttpStatus.OK, request.getContentType(), request.getResponseBody());
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
