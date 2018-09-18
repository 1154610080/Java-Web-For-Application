package com.web.site;

import org.springframework.stereotype.Repository;

import java.util.Hashtable;
import java.util.Map;

/**
 * UserRepository的实现类
 *  将所有用户都保存在内存中
 *
 * @author Egan
 * @date 2018/9/18 20:50
 **/
@Repository
public class InMemoryUserRepository implements UserRepository {

    private final Map<String, String> userDatabase = new Hashtable<>();

    public InMemoryUserRepository(){
        this.userDatabase.put("Nicholas", "passwd");
        this.userDatabase.put("John", "green");
        this.userDatabase.put("Egan", "admin");
    }

    @Override
    public String getPasswordForUser(String username) {
        return this.userDatabase.get(username);
    }
}
