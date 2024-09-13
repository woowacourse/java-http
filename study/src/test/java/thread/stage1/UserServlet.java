package thread.stage1;

import java.util.ArrayList;
import java.util.List;

public class UserServlet {

    private final List<User> users = new ArrayList<>();

    public void service(final User user) {
        join(user);
    }

    private synchronized void join(final User user) {
        if (!users.contains(user)) {
            try {
                Thread.sleep(1); // 의도적으로 컨텍스트 스위칭을 해도 테스트는 성공
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
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
