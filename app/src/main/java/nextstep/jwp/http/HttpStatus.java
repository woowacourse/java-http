package nextstep.jwp.http;

public enum HttpStatus {
    OK_200("200", "Ok"),
    FOUND_302("302", "Found"),
    UNAUTHORIZED_401("401", "Authorized");

    String number;
    String name;

    HttpStatus(String number, String name) {
        this.number = number;
        this.name = name;
    }
}
