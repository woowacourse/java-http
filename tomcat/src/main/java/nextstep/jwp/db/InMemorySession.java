package nextstep.jwp.db;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import nextstep.jwp.exception.UnauthorizedException;
import nextstep.jwp.model.User;

public class InMemorySession {
    private static final Map<User, UUID> session = new HashMap<>();

    public static String login(User user){
        UUID uuid = UUID.randomUUID();
        session.put(user,uuid);
        return uuid.toString();
    }
    public static boolean isLogin(String id){
        for(UUID uuid: session.values()){
            if(uuid.toString().equals(id)){
                return true;
            }
        }
        return false;
    }
}
