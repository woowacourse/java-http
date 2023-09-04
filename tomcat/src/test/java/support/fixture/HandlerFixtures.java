package support.fixture;

import java.util.List;
import org.apache.coyote.handler.Handler;
import org.apache.coyote.handler.LoginHandler;
import org.apache.coyote.handler.StaticFileHandler;
import org.apache.coyote.handler.WelcomePageHandler;

public class HandlerFixtures {

  public static List<Handler> handlers() {
    return List.of(
        new LoginHandler(),
        new StaticFileHandler(),
        new WelcomePageHandler()
    );
  }
}
