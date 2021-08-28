package nextstep.jwp.http.response;

public enum HttpStatus {
    OK(200),
    FOUND(302),
    UNAUTHORIZED(401);

    private int status;

    HttpStatus(int status) {
        this.status = status;
    }

    public boolean isOK(){
        return this.status == 200;
    }

    public boolean isFound(){
        return this.status == 302;
    }

    public boolean isUnauthorized(){
        return this.status == 401;
    }
}
