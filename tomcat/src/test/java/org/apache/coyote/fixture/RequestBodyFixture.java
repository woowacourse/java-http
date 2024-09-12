package org.apache.coyote.fixture;

import org.apache.coyote.request.body.RequestBody;

public class RequestBodyFixture {

    public static final RequestBody REGISTERED_USER_BODY = new RequestBody(
            "account=gugu&password=password&email=hkkang@woowahan.com"
    );

    public static final RequestBody NEW_USER_BODY = new RequestBody(
            "account=polla&password=password&email=hkkang@woowahan.com"
    );

    public static final RequestBody REGISTERED_LOGIN_USER_BODY = new RequestBody("account=gugu&password=password");

    public static final RequestBody NOT_REGISTER_LOGIN_USER_BODY = new RequestBody("account=polla&password=password");


}
