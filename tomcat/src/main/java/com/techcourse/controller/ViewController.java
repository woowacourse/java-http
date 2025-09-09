package com.techcourse.controller;

import com.techcourse.db.Session;
import com.techcourse.db.SessionManager;
import java.util.Map;

public class ViewController {

    private final SessionManager sessionManager;

    public ViewController(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public String getLoginPage(Map<String, String> headers) {
        if (existsSession(headers)) {
            return "index.html";
        }
        return "login.html";
    }

    private boolean existsSession(Map<String, String> headers) {
        String cookie = headers.get("Cookie");

        if (cookie == null) {
            return false;
        }

        String[] parsedCookie = cookie.split("; "); // Idea-1f980704=d1410481-d266-4764-a4dd-47a3d9d19f64; Pycharm-edf2faa0=91c6849a-33b8-4d86-a27b-bd16d51f090a; Webstorm-b369078d=8a15b985-71c2-42b0-96b4-eb3e64f0dfe5; JSESSIONID=d4d9915e-323d-43af-beb0-a60ac9e7c6b7
        for (String parsedValue : parsedCookie) {
            String[] splits = parsedValue.split("=", 2);
            String name = splits[0];
            String value = splits[1];

            if (name.equals("JSESSIONID")) {
                Session session = sessionManager.findSession(value);
                if (session == null) {
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    public String getRegisterPage() {
        return "register.html";
    }
}
