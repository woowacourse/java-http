package nextstep.jwp.request;

public class RequestUri {

    private static final String MAIN_PATH = "/";
    private static final String DEFAULT_FILE_PATH = "static/";
    private static final String DEFAULT_FILE = "index.html";
    private static final String QUERY_MARK = "?";
    private static final String QUERY_DELIMITER = "&";
    private static final String USER_INFO_DELIMITER = "=";

    private final String uri;

    public RequestUri(String uri) {
        this.uri = uri;
    }

    public FileName toFileName() {
        if (MAIN_PATH.equals(uri)) {
            return new FileName(DEFAULT_FILE_PATH + DEFAULT_FILE);
        }
        return new RequestPath(uri.substring(1)).toFileName();
    }

    public boolean isLogin() {
        return uri.startsWith("/login") && isQueryMark();
    }

    public boolean isQueryMark() {
        return uri.contains(QUERY_MARK);
    }

    public UserInfo getUserInfo() {
        String queryString = uri.substring(uri.indexOf(QUERY_MARK) + 1);
        String[] splitQueryString = queryString.split(QUERY_DELIMITER);

        String account = splitQueryString[0].split(USER_INFO_DELIMITER)[1];
        String password = splitQueryString[1].split(USER_INFO_DELIMITER)[1];

        return new UserInfo(account, password);
    }

    public String getUri() {
        return uri;
    }
}
