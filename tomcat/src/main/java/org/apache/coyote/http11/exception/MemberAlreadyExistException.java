package org.apache.coyote.http11.exception;

public class MemberAlreadyExistException extends IllegalStateException{
    private static final String MESSAGE = "이미 존재하는 계정입니다. 입력한 account: ";

    public MemberAlreadyExistException(final String account) {
        super(MESSAGE + account);
    }

    public MemberAlreadyExistException(final String account, final Throwable cause) {
        super(MESSAGE + account, cause);
    }

}
