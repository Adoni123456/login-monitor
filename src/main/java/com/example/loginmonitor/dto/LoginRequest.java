package com.example.loginmonitor.dto;

import com.example.loginmonitor.entity.LoginAttempt.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class LoginRequest {

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "IP address is required")
    private String ipAddress;

    @NotNull(message = "Status is required")
    private Status status;

    public String getUsername() {
        return username;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public Status getStatus() {
        return status;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}