package nextstep.jwp.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginResult {

    private static final Logger log = LoggerFactory.getLogger(LoginResult.class);

    private final String statusCode;
    private final String reasonPhrase;
    private final String location;

    private LoginResult(final String statusCode, final String reasonPhrase, final String location) {
        this.statusCode = statusCode;
        this.reasonPhrase = reasonPhrase;
        this.location = location;
    }

    public static LoginResult from(final User user, final String password) {
        if (user.checkPassword(password)) {
            log.info(user.toString());
            return new LoginResult("302", "Found", "/index.html");
        }
        return new LoginResult("302", "Found", "/401.html");
    }

    public String getStatusCode() {
        return statusCode;
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }

    public String getLocation() {
        return location;
    }
}
