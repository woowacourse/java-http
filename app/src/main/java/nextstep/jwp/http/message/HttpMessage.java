package nextstep.jwp.http.message;

public interface HttpMessage {
    MessageHeader getHeader();

    MessageBody getBody();

    byte[] toBytes();
}
