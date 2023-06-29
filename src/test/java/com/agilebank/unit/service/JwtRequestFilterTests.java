package com.agilebank.unit.service;

import com.agilebank.service.JwtRequestFilter;
import com.agilebank.service.JwtUserDetailsService;
import com.agilebank.util.JwtTokenUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class JwtRequestFilterTests {

    @InjectMocks
    private JwtRequestFilter jwtRequestFilter;

    @Mock
    private JwtUserDetailsService jwtUserDetailsService;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    // Have to mock the arguments too since I'll have to return
    // things from calls to them and they are interfaces.
    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private HttpServletResponse httpServletResponse;

    @Mock
    private FilterChain filterChain;

    @Test
    public void whenHeaderHasAuthBearerToken_andUsernameFetchedFromToken_andUserDetailsFoundInDb_thenSecurityContextHolderAuthenticatesUser(){

    }
}
