package nextstep.jwp.exception;

public class AlreadyRegisteredUser extends RuntimeException {

    private static final String MESSAGE = "이미 존재하는 계정입니다. account = ";

    public AlreadyRegisteredUser(String account) {
        super(MESSAGE + account);
    }
}
