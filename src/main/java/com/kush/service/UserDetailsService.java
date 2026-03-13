package com.kush.service;

import com.kush.entity.SecurityUser;
import com.kush.entity.User;
import com.kush.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final UserRepository repository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOpt= repository.findByEmail(username);
        if(userOpt.isPresent()){
            return new SecurityUser(userOpt.get());
        }
        else throw new UsernameNotFoundException(username);
    }

}