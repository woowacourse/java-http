package nextstep.jwp.model.httpMessage.response;


import nextstep.jwp.model.httpMessage.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ResponseLine {

    private static final Logger LOG = LoggerFactory.getLogger(ResponseLine.class);
    private static final String PROTOCOL = "HTTP/1.1";
    private final HttpStatus httpStatus;

    public ResponseLine(HttpStatus status) {
        this.httpStatus = status;
        LOG.debug("Response line : {}", this);
    }

    @Override
    public String toString() {
        return PROTOCOL + " " + httpStatus + " ";
    }
}
