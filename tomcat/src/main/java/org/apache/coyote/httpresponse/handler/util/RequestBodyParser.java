package org.apache.coyote.httpresponse.handler.util;

import nextstep.jwp.model.User;
import org.apache.coyote.httprequest.QueryString;
import org.apache.coyote.httprequest.RequestBody;

public class RequestBodyParser {

    public static User parse(final RequestBody requestBody) {
        final String content = requestBody.getContents();
        final QueryString parsedValue = QueryString.from(content);
        final String account = parsedValue.getValue("account");
        final String password = parsedValue.getValue("password");
        final String email = parsedValue.getValue("email");
        return new User(account, password, email);
    }
}
