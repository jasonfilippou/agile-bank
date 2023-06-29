package com.agilebank.service;

import com.agilebank.model.user.UserDao;
import com.agilebank.model.user.UserDto;
import com.agilebank.persistence.UserRepository;
import java.util.ArrayList;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class JwtUserDetailsService implements UserDetailsService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    
    @Autowired
    public JwtUserDetailsService(UserRepository userRepository, PasswordEncoder encoder){
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserDao> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            // Return an instance of org.springframework.security.core.userdetails.User
            return new User(user.get().getUsername(), user.get().getPassword(), new ArrayList<>());
        } else {
            throw new UsernameNotFoundException("User with username: " + username + " not found.");
        }
    }
    
    public UserDto save(UserDto newUser){
        UserDao newUserDao = new UserDao(newUser.getUsername(), encoder.encode(newUser.getPassword()));
        newUserDao = userRepository.save(newUserDao);
        return new UserDto(newUserDao.getUsername(), newUserDao.getPassword());
    }
}