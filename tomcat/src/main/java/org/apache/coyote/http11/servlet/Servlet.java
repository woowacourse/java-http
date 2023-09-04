package org.apache.coyote.http11.servlet;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import nextstep.jwp.exception.UnauthorizedException;
import org.apache.coyote.http11.exception.NoSuchApiException;
import org.apache.coyote.http11.handler.Configurer;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.Response;

public class Servlet {
    public static String getResponse(Request request){
        try {
            return Configurer.handle(request);
        } catch (UnauthorizedException unauthorizedException) {
            return Response.badResponse(HttpStatus.UNAUTHORIZED).redirect(getFile("401.html"),"401.html").getResponse();
        } catch (NoSuchApiException e){
            return Response.badResponse(HttpStatus.NOTFOUND).redirect(getFile("404.html"),"404.html").getResponse();
        }
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
