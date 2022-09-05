package nextstep.jwp.views;

import nextstep.jwp.db.InMemoryUserRepository;
import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http11.Http11Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestLoginUserOutput {

    private static final Logger log = LoggerFactory.getLogger(RequestLoginUserOutput.class);

    public static void printRequestLoginUser(final HttpRequest httpRequest){
        if(!httpRequest.hasQuery()){
            return;
        }
        final String account = httpRequest.getQueryByValue("account");
        InMemoryUserRepository.findByAccount(account)
                .map(it -> it.checkPassword(httpRequest.getQueryByValue("password")))
                .ifPresent(it -> log.info(it.toString()));
    }

}
