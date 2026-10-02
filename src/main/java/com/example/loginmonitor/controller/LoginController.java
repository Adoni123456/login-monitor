package com.example.loginmonitor.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.loginmonitor.dto.LoginRequest;
import com.example.loginmonitor.entity.LoginAttempt;
import com.example.loginmonitor.entity.LoginAttempt.Status;
import com.example.loginmonitor.entity.SuspiciousActivity;
import com.example.loginmonitor.service.LoginService;

import jakarta.validation.Valid;

@RestController
@CrossOrigin(origins = {
    "http://localhost:5173",
    "http://localhost:5174"
})
@RequestMapping({"/api", ""})
public class LoginController {

    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    // POST /api/login
    // POST /login
    @PostMapping("/login")
    public ResponseEntity<LoginAttempt> login(
            @Valid @RequestBody LoginRequest request) {

        LoginAttempt result =
                loginService.recordLoginAttempt(request);

        return ResponseEntity.ok(result);
    }

    // GET /api/login
    // GET /login
    @GetMapping("/login")
    public ResponseEntity<List<LoginAttempt>> getLoginAttempts(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String ipAddress,
            @RequestParam(required = false) Status status) {

        List<LoginAttempt> attempts =
                loginService.getLoginAttempts(
                        username,
                        ipAddress,
                        status
                );

        return ResponseEntity.ok(attempts);
    }

    // GET /api/suspicious
    // GET /suspicious
    @GetMapping("/suspicious")
    public ResponseEntity<List<SuspiciousActivity>> getSuspiciousActivities() {

        return ResponseEntity.ok(
                loginService.getSuspiciousActivities()
        );
    }
}