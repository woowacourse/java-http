package nextstep.jwp.http.message;

public interface HttpMessage {
    StartLine getStartLine();

    MessageHeader getHeader();

    MessageBody getBody();

    byte[] toBytes();
}
