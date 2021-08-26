package nextstep.jwp.http.message;

import java.util.Optional;

public interface HttpMessage {
    MessageHeader getHeader();

    Optional<MessageBody> getBody();
}
