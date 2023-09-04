package nextstep.jwp.controller;

import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.Response;

public class ViewController {
    public static Response getIndex(Request request){
        return Response.builder()
                .status(HttpStatus.OK)
                .build();
    }

    public static Response getRegister(Request request){
        return Response.builder()
                .status(HttpStatus.OK)
                .build();
    }
}
