package nextstep.jwp.http;

public enum HttpStatus {
    OK_200("200", "OK"),
    FOUND_302("302", "FOUND"),
    UNAUTHORIZED_401("401", "UNAUTHORIZED"),
    NOT_FOUND_404("404", "NOT FOUND");

    final String number;
    final String name;

    HttpStatus(String number, String name) {
        this.number = number;
        this.name = name;
    }

    @Override
    public String toString() {
        return number + " " + name + " ";
    }
}
