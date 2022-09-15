package nextstep.jwp.exception;

public class QueryStringFormatException extends RuntimeException {

    public QueryStringFormatException() {
        super("쿼리문 형식이 잘못됬습니다");
    }
}
