package com.simplecoding.michelin_back.admin.service;

import com.simplecoding.michelin_back.admin.dto.AdminLogDto;
import com.simplecoding.michelin_back.admin.entity.AdminLog;
import com.simplecoding.michelin_back.admin.repository.AdminLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminLogService {

    private final AdminLogRepository adminLogRepository;

    /** 로그 기록 (다른 서비스에서 호출) */
    @Transactional
    public void log(Long adminId, String action, Long targetId, String detail) {
        adminLogRepository.save(AdminLog.builder()
                .adminId(adminId)
                .adminAction(action)
                .targetId(targetId)
                .actionDetail(detail)
                .build());
    }

    public Page<AdminLogDto.Response> getAll(Pageable pageable) {
        return adminLogRepository.findAllByOrderByInsertTimeDesc(pageable)
                .map(this::toResponse);
    }

    public Page<AdminLogDto.Response> getByAdmin(Long adminId, Pageable pageable) {
        return adminLogRepository.findByAdminIdOrderByInsertTimeDesc(adminId, pageable)
                .map(this::toResponse);
    }

    private AdminLogDto.Response toResponse(AdminLog log) {
        return AdminLogDto.Response.builder()
                .adminLogId(log.getAdminLogId())
                .adminId(log.getAdminId())
                .targetId(log.getTargetId())
                .adminAction(log.getAdminAction())
                .actionDetail(log.getActionDetail())
                .insertTime(log.getInsertTime())
                .build();
    }
}
