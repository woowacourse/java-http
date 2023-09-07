package thread.stage1;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserServlet {

    private static final Logger log = LoggerFactory.getLogger(UserServlet.class);

    private final List<User> users = new ArrayList<>();

    public void service(final User user) {
        join(user);
        log.info("Join User : {}", user);
    }

    private void join(final User user) {
        if (!users.contains(user)) {
            users.add(user);
        }
    }

    public int size() {
        return users.size();
    }

    public List<User> getUsers() {
        return users;
    }
}
