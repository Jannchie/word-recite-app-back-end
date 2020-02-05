package com.jannchie.word.security;

import java.util.ArrayList;
import java.util.Collection;

import com.jannchie.word.constant.ResultEnum;
import com.jannchie.word.model.User;

import com.jannchie.word.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * @author Jannchie
 */
@Component
public class UserAuthenticationProvider implements AuthenticationProvider {


    private final MongoTemplate mongoTemplate;
    private final BCryptPasswordEncoder bcryptPasswordEncoder;

    @Autowired
    public UserAuthenticationProvider(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
        this.bcryptPasswordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public Authentication authenticate(Authentication authentication) {
        String name = authentication.getPrincipal().toString();
        String password = authentication.getCredentials().toString();

        User user = UserUtils.getUserByUsername(name);
        if (user != null && bcryptPasswordEncoder.matches(password,user.getPassword())) {
            Collection<GrantedAuthority> authorityCollection = new ArrayList<>();
            authorityCollection.add(new SimpleGrantedAuthority("USER"));
            return new UsernamePasswordAuthenticationToken(name, password, authorityCollection);
        }
        throw new BadCredentialsException(ResultEnum.LOGIN_FAILED.getMsg());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

}