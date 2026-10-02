package com.example.loginmonitor.repository;

import com.example.loginmonitor.entity.LoginAttempt;
import com.example.loginmonitor.entity.LoginAttempt.Status;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface LoginAttemptRepository
        extends JpaRepository<LoginAttempt, Long> {

    // GET /login filters
    @Query("""
        SELECT l FROM LoginAttempt l
        WHERE (:username IS NULL OR l.username = :username)
        AND (:ipAddress IS NULL OR l.ipAddress = :ipAddress)
        AND (:status IS NULL OR l.status = :status)
        ORDER BY l.timestamp DESC
        """)
    List<LoginAttempt> findFiltered(
            @Param("username") String username,
            @Param("ipAddress") String ipAddress,
            @Param("status") Status status
    );

    // Brute-force detection by IP
    long countByIpAddressAndStatusAndTimestampAfter(
            String ipAddress,
            Status status,
            LocalDateTime timestamp
    );

    // Brute-force detection by username
    long countByUsernameAndStatusAndTimestampAfter(
            String username,
            Status status,
            LocalDateTime timestamp
    );

    // Previous attempts from same IP, excluding current attempt
    @Query("""
        SELECT l FROM LoginAttempt l
        WHERE l.ipAddress = :ipAddress
        AND l.timestamp < :timestamp
        AND l.timestamp >= :windowStart
        ORDER BY l.timestamp DESC
        """)
    List<LoginAttempt> findRecentByIpAddress(
            @Param("ipAddress") String ipAddress,
            @Param("timestamp") LocalDateTime timestamp,
            @Param("windowStart") LocalDateTime windowStart
    );

    // Previous attempts from same username, excluding current attempt
    @Query("""
        SELECT l FROM LoginAttempt l
        WHERE l.username = :username
        AND l.timestamp < :timestamp
        AND l.timestamp >= :windowStart
        ORDER BY l.timestamp DESC
        """)
    List<LoginAttempt> findRecentByUsername(
            @Param("username") String username,
            @Param("timestamp") LocalDateTime timestamp,
            @Param("windowStart") LocalDateTime windowStart
    );
}