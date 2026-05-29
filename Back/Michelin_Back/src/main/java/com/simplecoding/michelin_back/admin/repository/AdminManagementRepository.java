package com.simplecoding.michelin_back.admin.repository;

import com.simplecoding.michelin_back.admin.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminManagementRepository extends JpaRepository<Admin, Long> {
    boolean existsByMemberId(Long memberId);
}
