package com.simplecoding.michelin_back.admin.repository;

import com.simplecoding.michelin_back.admin.entity.AdminLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminLogRepository extends JpaRepository<AdminLog, Long> {

    // 특정 관리자의 활동 로그
    Page<AdminLog> findByAdmin_AdminIdOrderByCreatedAtDesc(Long adminId, Pageable pageable);

    // 액션 타입별 로그 (예: REVIEW_DELETE만 조회)
    Page<AdminLog> findByActionOrderByCreatedAtDesc(String action, Pageable pageable);
}
