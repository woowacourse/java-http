package nextstep.jwp.framework.message;

public interface MessageHeader {
    HeaderFields getHeaderFields();

    byte[] toBytes();
}
