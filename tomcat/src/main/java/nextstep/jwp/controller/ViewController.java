package nextstep.jwp.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.Response;
import org.apache.coyote.http11.servlet.Servlet;

public class ViewController {
    public static Response getLogin(){
        return Response.builder()
                .status(HttpStatus.OK)
                .contentType("html")
                .responseBody(getFile("login.html"))
                .build();
    }

    public static Response getRegister(){
        return Response.builder()
                .status(HttpStatus.OK)
                .contentType("html")
                .responseBody(getFile("register.html"))
                .build();
    }

    public static Response getVoid(){
        return Response.builder()
                .status(HttpStatus.OK)
                .responseBody("Hello world!")
                .contentType("html")
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
