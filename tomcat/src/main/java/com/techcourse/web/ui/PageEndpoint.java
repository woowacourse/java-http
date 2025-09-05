package com.techcourse.web.ui;

public enum PageEndpoint {
    BASIC("/","/index.html"),
    LOGIN("/login","/login.html");

    private final String endPoint;
    private final String page;

    PageEndpoint(final String endPoint, final String page) {
        this.endPoint = endPoint;
        this.page = page;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public String getPage() {
        return page;
    }

    public static String findPageByPath(String path){
        for (PageEndpoint pageEndpoint : values()) {
            if (path.equals(pageEndpoint.endPoint)) {
                return pageEndpoint.page;
            }
        }
        return path;
    }
}
