package com.example.demo.service;

import com.example.demo.dto.SignupRequestDto;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    public SignupRequestDto signup(SignupRequestDto signupRequestDto) {
        if (userRepository.findByUsername(signupRequestDto.username()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }
        User user = new User();
        user.setName(signupRequestDto.name());
        user.setUsername(signupRequestDto.username());
        user.setPassword(passwordEncoder.encode(signupRequestDto.password()));
        userRepository.save(user);
        return signupRequestDto;
    }
}
