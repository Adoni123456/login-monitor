package com.example.loginmonitor.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.loginmonitor.dto.LoginRequest;
import com.example.loginmonitor.entity.LoginAttempt;
import com.example.loginmonitor.entity.LoginAttempt.Status;
import com.example.loginmonitor.entity.SuspiciousActivity;
import com.example.loginmonitor.repository.LoginAttemptRepository;
import com.example.loginmonitor.repository.SuspiciousActivityRepository;

@Service
public class LoginService {

    private static final int FAILURE_THRESHOLD = 5;
    private static final int TIME_WINDOW_MINUTES = 10;

    private final LoginAttemptRepository loginAttemptRepository;
    private final SuspiciousActivityRepository suspiciousActivityRepository;

    public LoginService(
            LoginAttemptRepository loginAttemptRepository,
            SuspiciousActivityRepository suspiciousActivityRepository) {

        this.loginAttemptRepository = loginAttemptRepository;
        this.suspiciousActivityRepository = suspiciousActivityRepository;
    }

    // Record login attempt
    public LoginAttempt recordLoginAttempt(LoginRequest request) {

        LocalDateTime now = LocalDateTime.now();

        LoginAttempt loginAttempt = new LoginAttempt();

        loginAttempt.setUsername(request.getUsername().trim());
        loginAttempt.setIpAddress(request.getIpAddress().trim());
        loginAttempt.setStatus(request.getStatus());
        loginAttempt.setTimestamp(now);

        LoginAttempt savedAttempt =
                loginAttemptRepository.save(loginAttempt);

        // Check suspicious behaviour
        detectSuspiciousActivity(savedAttempt);

        return savedAttempt;
    }

    // Detect suspicious activity
    private void detectSuspiciousActivity(LoginAttempt attempt) {

        LocalDateTime windowStart =
                attempt.getTimestamp()
                        .minusMinutes(TIME_WINDOW_MINUTES);

        /*
         * BRUTE FORCE DETECTION
         *
         * 5 failed attempts from the same IP
         * within 10 minutes.
         */
        if (attempt.getStatus() == Status.FAILURE) {

            long failedFromIp =
                    loginAttemptRepository
                            .countByIpAddressAndStatusAndTimestampAfter(
                                    attempt.getIpAddress(),
                                    Status.FAILURE,
                                    windowStart
                            );

            if (failedFromIp == FAILURE_THRESHOLD) {

                saveSuspiciousActivity(
                        attempt,
                        "5 failed login attempts from the same IP within 10 minutes"
                );
            }

            /*
             * 5 failed attempts for the same username
             * within 10 minutes.
             */
            long failedForUser =
                    loginAttemptRepository
                            .countByUsernameAndStatusAndTimestampAfter(
                                    attempt.getUsername(),
                                    Status.FAILURE,
                                    windowStart
                            );

            if (failedForUser == FAILURE_THRESHOLD) {

                saveSuspiciousActivity(
                        attempt,
                        "5 failed login attempts for the same user within 10 minutes"
                );
            }
        }

        /*
         * UNUSUAL SUCCESS DETECTION
         *
         * If a successful login occurs immediately after
         * 5 failed attempts within 10 minutes, mark it suspicious.
         *
         * The repository query excludes the current SUCCESS attempt.
         */
        if (attempt.getStatus() == Status.SUCCESS) {

            // Check previous attempts from the same IP
            List<LoginAttempt> recentIpAttempts =
                    loginAttemptRepository.findRecentByIpAddress(
                            attempt.getIpAddress(),
                            attempt.getTimestamp(),
                            windowStart
                    );

            if (areAllFailures(recentIpAttempts, FAILURE_THRESHOLD)) {

                saveSuspiciousActivity(
                        attempt,
                        "Successful login immediately following multiple failed attempts from the same IP"
                );
            }

            // Check previous attempts for the same username
            List<LoginAttempt> recentUserAttempts =
                    loginAttemptRepository.findRecentByUsername(
                            attempt.getUsername(),
                            attempt.getTimestamp(),
                            windowStart
                    );

            if (areAllFailures(recentUserAttempts, FAILURE_THRESHOLD)) {

                saveSuspiciousActivity(
                        attempt,
                        "Successful login immediately following multiple failed attempts for the same user"
                );
            }
        }
    }

    // Check whether the required number of attempts are all failures
    private boolean areAllFailures(
            List<LoginAttempt> attempts,
            int requiredCount) {

        if (attempts.size() < requiredCount) {
            return false;
        }

        // Only examine the required number of most recent attempts
        for (int i = 0; i < requiredCount; i++) {

            if (attempts.get(i).getStatus() != Status.FAILURE) {
                return false;
            }
        }

        return true;
    }

    // Save suspicious activity
    private void saveSuspiciousActivity(
            LoginAttempt attempt,
            String reason) {

        SuspiciousActivity suspiciousActivity =
                new SuspiciousActivity();

        suspiciousActivity.setIpAddress(attempt.getIpAddress());
        suspiciousActivity.setUsername(attempt.getUsername());
        suspiciousActivity.setReason(reason);
        suspiciousActivity.setTimestamp(LocalDateTime.now());

        suspiciousActivityRepository.save(suspiciousActivity);
    }

    // GET login attempts
    public List<LoginAttempt> getLoginAttempts(
            String username,
            String ipAddress,
            Status status) {

        return loginAttemptRepository.findFiltered(
                username,
                ipAddress,
                status
        );
    }

    // GET suspicious activities
    public List<SuspiciousActivity> getSuspiciousActivities() {

        return suspiciousActivityRepository.findAll();
    }
}