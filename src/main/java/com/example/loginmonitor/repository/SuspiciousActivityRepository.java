package com.example.loginmonitor.repository;

import com.example.loginmonitor.entity.SuspiciousActivity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SuspiciousActivityRepository
        extends JpaRepository<SuspiciousActivity, Long> {
}