package thread.stage1;

import java.util.ArrayList;
import java.util.List;

public class UserServlet {

    private final List<User> users = new ArrayList<>();

    public void service(final User user) {
        join(user);
    }

    private void join(final User user) {
        // 두 스레드가 중복 체크를 수행한 시점에서 users 리스트가 모두 비어있었다는 점
        // "읽기-확인-쓰기" 패턴에서 발생하는 전형적인 race condition의 메커니즘
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
