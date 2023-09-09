package nextstep.jwp.controller;

import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.Response;
import org.apache.coyote.http11.util.Resource;

public class ViewController {

    private ViewController(){}

    public static Response getLogin(){
        return Response.builder()
                .status(HttpStatus.OK)
                .contentType("html")
                .responseBody(Resource.getFile("login.html"))
                .build();
    }

    public static Response getRegister(){
        return Response.builder()
                .status(HttpStatus.OK)
                .contentType("html")
                .responseBody(Resource.getFile("register.html"))
                .build();
    }

    public static Response getVoid(){
        return Response.builder()
                .status(HttpStatus.OK)
                .responseBody("Hello world!")
                .contentType("html")
                .build();
    }
}
