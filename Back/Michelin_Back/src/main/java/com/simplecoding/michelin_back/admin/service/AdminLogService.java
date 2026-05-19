package com.simplecoding.michelin_back.admin.service;

import com.simplecoding.michelin_back.admin.entity.Admin;
import com.simplecoding.michelin_back.admin.entity.AdminLog;
import com.simplecoding.michelin_back.admin.repository.AdminLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminLogService {

    private final AdminLogRepository adminLogRepository;

    // 로그 기록 (다른 서비스에서 호출)
    @Transactional
    public void log(Admin admin, String action, String targetType, Long targetId, String detail) {
        AdminLog adminLog = AdminLog.builder()
                .admin(admin)
                .action(action)
                .targetType(targetType)
                .targetId(targetId)
                .detail(detail)
                .build();
        adminLogRepository.save(adminLog);
    }

    // 관리자 활동 로그 목록
    @Transactional(readOnly = true)
    public Page<AdminLog> getLogs(Pageable pageable) {
        return adminLogRepository.findAll(pageable);
    }

    // 특정 관리자 로그
    @Transactional(readOnly = true)
    public Page<AdminLog> getLogsByAdmin(Long adminId, Pageable pageable) {
        return adminLogRepository.findByAdmin_AdminIdOrderByCreatedAtDesc(adminId, pageable);
    }
}
