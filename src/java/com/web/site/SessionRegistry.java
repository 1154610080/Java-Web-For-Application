package com.web.site;

import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.function.Consumer;

public interface SessionRegistry {

    void addSession(HttpSession session);

    void updateSession(HttpSession session, String oldId);

    void removeSession(HttpSession session);

    int getNumberOfSessions();

    List<HttpSession> getAllSessions();

    void registerOnRemoveCallback(Consumer<HttpSession> callback);

    void deregisterOnRemoveCallback(Consumer<HttpSession> callback);
}
