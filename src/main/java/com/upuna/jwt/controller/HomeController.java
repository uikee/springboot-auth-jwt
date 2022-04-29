package com.upuna.jwt.controller;

import com.upuna.jwt.model.JWTRequest;
import com.upuna.jwt.model.JWTResponse;
import com.upuna.jwt.service.UserService;
import com.upuna.jwt.util.JWTUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    private final JWTUtility jwtUtility;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    public HomeController(JWTUtility jwtUtility, AuthenticationManager authenticationManager, UserService userService) {
        this.jwtUtility = jwtUtility;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }

    @GetMapping("/")
    public String home() {
        return "Welcome to Upuna's auth";
    }

    @PostMapping("authenticate")
    public JWTResponse authenticate(@RequestBody JWTRequest jwtRequest) throws Exception{

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            jwtRequest.getUsername(),
                            jwtRequest.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID CREDENTIALS");
        }

        final UserDetails userDetails = userService.loadUserByUsername(jwtRequest.getUsername());

        final String token = jwtUtility.generateToken(userDetails);

        return new JWTResponse(token);

    }
}
