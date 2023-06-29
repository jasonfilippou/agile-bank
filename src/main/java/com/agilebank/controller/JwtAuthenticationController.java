package com.agilebank.controller;


import com.agilebank.model.jwt.JwtRequest;
import com.agilebank.model.jwt.JwtResponse;
import com.agilebank.model.user.UserDto;
import com.agilebank.service.JwtAuthenticationService;
import com.agilebank.service.JwtUserDetailsService;
import com.agilebank.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bankapi")
@CrossOrigin
public class JwtAuthenticationController {

    private final JwtTokenUtil jwtTokenUtil;
    private final JwtUserDetailsService userDetailsService;

    private final JwtAuthenticationService jwtAuthenticationService;

    @Autowired
    public JwtAuthenticationController(JwtTokenUtil jwtTokenUtil,
                                       JwtUserDetailsService jwtUserDetailsService,
                                        JwtAuthenticationService jwtAuthenticationService){
        this.jwtTokenUtil = jwtTokenUtil;
        this.userDetailsService = jwtUserDetailsService;
        this.jwtAuthenticationService = jwtAuthenticationService;
    }
    @PostMapping(value = "/authenticate")
    public ResponseEntity<JwtResponse> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {

        jwtAuthenticationService.authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());
        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(authenticationRequest.getUsername());
        final String token = jwtTokenUtil.generateToken(userDetails);
        return ResponseEntity.ok(new JwtResponse(token));
    }

    @PostMapping(value = "/register")
    public ResponseEntity<UserDto> registerUser(@RequestBody UserDto user){
        return ResponseEntity.ok(userDetailsService.save(user));
    }
}