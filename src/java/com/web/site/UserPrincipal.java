package com.web.site;

import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.security.Principal;

/**
 * 用户当事人类
 *
 * @author Egan
 * @date 2018/9/18 21:02
 **/
public class UserPrincipal implements Principal, Cloneable, Serializable {

    private final String username;

    public UserPrincipal(String username) {
        this.username = username;
    }

    @Override
    public String getName() {
        return this.username;
    }

    @Override
    public int hashCode() {
        return this.username.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof UserPrincipal &&
                ((UserPrincipal)other).username.equals(this.username);
    }

    @Override
    protected UserPrincipal clone(){
        try {
            return (UserPrincipal)super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);  //不可能发生
        }
    }

    @Override
    public String toString() {
        return this.username;
    }

    public static Principal getPrincipal(HttpSession session){
        return session == null ? null :
                (Principal) session.getAttribute("com.web.user.principal");
    }

    public static void setPrincipal(HttpSession session, Principal principal){
        session.setAttribute("com.web.user.principal", principal);
    }
}
