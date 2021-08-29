package nextstep.jwp.handler;

import nextstep.jwp.http.response.HttpStatus;

public class Model {
    private final HttpStatus httpStatus;
    private final String location;

    public static Model of(HttpStatus httpStatus){
        return new Model(httpStatus, "");
    }

    public static Model of(HttpStatus httpStatus, String location){
        return new Model(httpStatus, location);
    }

    private Model(HttpStatus httpStatus, String location) {
        this.httpStatus = httpStatus;
        this.location = location;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getLocation() {
        return location;
    }
}
