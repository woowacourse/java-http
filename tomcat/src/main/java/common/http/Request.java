package common.http;

public interface Request {

    HttpMethod getHttpMethod();

    String getVersionOfTheProtocol();

    String getAccount();

    String getPassword();

    Session getSession(boolean create);

    Session getSession();

    String getCookie();

    String getPath();

    boolean hasValidSession();

    String getEmail();

    void addSession(Session session);
}
