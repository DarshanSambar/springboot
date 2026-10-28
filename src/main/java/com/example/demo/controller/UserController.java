package com.example.demo.controller;

import com.example.demo.dto.LoginRequestDto;
import com.example.demo.dto.SignupRequestDto;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private SecurityContextRepository securityContextRepository;

    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequestDto loginRequest,
                        HttpServletRequest request,
                        HttpServletResponse response) {

        // 1. Authenticate user credentials
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                        loginRequest.username(),
                        loginRequest.password()
                );

        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        // 2. Create SecurityContext and set authentication
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        // 3. Explicitly persist to HttpSession so JSESSIONID cookie is created and sent to the client
        securityContextRepository.saveContext(context, request, response);

        return "Login successful! Session ID: " + request.getSession().getId();
    }

    @PostMapping("/signup")
    public SignupRequestDto signup(@RequestBody SignupRequestDto signupRequest) {
        return userService.signup(signupRequest);
    }
}
