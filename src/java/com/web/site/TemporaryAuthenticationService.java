package com.web.site;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.security.Principal;

/**
 * AuthenticationService的实现类
 *
 * @author Egan
 * @date 2018/9/18 20:58
 **/
@Service
public class TemporaryAuthenticationService implements AuthenticationService {

    private static final Logger log = LogManager.getLogger();

    @Inject UserRepository userRepository;

    @Override
    public Principal authenticate(String username, String password) {
        String currentPassword = this.userRepository.getPasswordForUser(username);
        if(currentPassword == null){
            log.warn("Authenticate failed for non-existing user {}.", username);
            return null;
        }
        if(!currentPassword.equals(password)){
            log.warn("Authenticate failed for user {}.", username);
            return null;
        }

        log.debug("User {} successfully authenticated.", username);
        return new UserPrincipal(username);
    }
}
