package com.simplecoding.michelin_back.admin.service;

import com.simplecoding.michelin_back.admin.dto.NoticeDto;
import com.simplecoding.michelin_back.admin.entity.Admin;
import com.simplecoding.michelin_back.admin.entity.Notice;
import com.simplecoding.michelin_back.admin.repository.AdminRepository;
import com.simplecoding.michelin_back.admin.repository.NoticeRepository;
import com.simplecoding.michelin_back.common.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final AdminRepository adminRepository;
    private final AdminLogService adminLogService;

    // 공지사항 목록 (삭제되지 않은 것만, 고정 공지 상단)
    @Transactional(readOnly = true)
    public Page<Notice> getNotices(Pageable pageable) {
        return noticeRepository.findByDeletYnOrderByFixYnDescInsertTimeDesc("N", pageable);
    }

    // 공지사항 상세
    @Transactional(readOnly = true)
    public Notice getNotice(Long noticeId) {
        return noticeRepository.findById(noticeId)
                .filter(n -> "N".equals(n.getDeletYn()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 공지사항입니다."));
    }

    // 공지사항 등록
    @Transactional
    public Notice createNotice(CustomUserDetails userDetails, NoticeDto.Request request) {
        Admin admin = getAdmin(userDetails.getMemberId());
        Notice notice = Notice.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .writer(admin)
                .fixYn(request.getFixYn())
                .build();
        Notice saved = noticeRepository.save(notice);
        adminLogService.log(admin, "NOTICE_CREATE", "NOTICE", saved.getNoticeId(), request.getTitle());
        return saved;
    }

    // 공지사항 수정
    @Transactional
    public Notice updateNotice(CustomUserDetails userDetails, Long noticeId, NoticeDto.Request request) {
        Admin admin = getAdmin(userDetails.getMemberId());
        Notice notice = getNotice(noticeId);
        notice.update(request.getTitle(), request.getContent(), request.getFixYn());
        adminLogService.log(admin, "NOTICE_UPDATE", "NOTICE", noticeId, request.getTitle());
        return notice;
    }

    // 공지사항 삭제 (소프트 딜리트)
    @Transactional
    public void deleteNotice(CustomUserDetails userDetails, Long noticeId) {
        Admin admin = getAdmin(userDetails.getMemberId());
        Notice notice = getNotice(noticeId);
        notice.delete();
        adminLogService.log(admin, "NOTICE_DELETE", "NOTICE", noticeId, notice.getTitle());
    }

    private Admin getAdmin(Long memberId) {
        return adminRepository.findByMember_MemberId(memberId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "관리자 권한이 없습니다."));
    }
}
