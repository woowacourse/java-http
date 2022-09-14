package http.exception;

public class QueryStringFormatException extends RuntimeException {

    public QueryStringFormatException() {
        super("query string이 형식에 맞지 않습니다.");
    }
}
