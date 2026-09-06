package com.userservice.netflux.user.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.userservice.netflux.user.dto.LoginRequest;
import com.userservice.netflux.user.dto.LoginResponse;
import com.userservice.netflux.user.dto.RegisterRequest;
import com.userservice.netflux.user.exceptions.UsernameAlreadyExistsException;
import com.userservice.netflux.user.mapper.UserMapper;
import com.userservice.netflux.user.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public UserService(UserRepository userRepository,
                        PasswordEncoder passwordEncoder,
                        AuthenticationManager authenticationManager,
                        JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Transactional
    public void register(RegisterRequest request) {
        if (userRepository.existsByUsernameIgnoreCase(request.username())) {
            throw new UsernameAlreadyExistsException(request.username());
        }
        var user = UserMapper.toEntity(request, passwordEncoder.encode(request.password()));
        userRepository.save(user);
    }

    public LoginResponse login(LoginRequest request) {
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );
        var principal = (UserPrincipal) authentication.getPrincipal();
        return new LoginResponse(jwtService.generateToken(principal.getUserId()), principal.getName());
    }

}
