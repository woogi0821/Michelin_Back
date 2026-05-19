package com.simplecoding.michelin_back.admin.repository;

import com.simplecoding.michelin_back.admin.entity.AdminLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminLogRepository extends JpaRepository<AdminLog, Long> {
    Page<AdminLog> findByAdminIdOrderByInsertTimeDesc(Long adminId, Pageable pageable);
    Page<AdminLog> findAllByOrderByInsertTimeDesc(Pageable pageable);
}
