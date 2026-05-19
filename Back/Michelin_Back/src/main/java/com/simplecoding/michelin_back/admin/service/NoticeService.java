package com.simplecoding.michelin_back.admin.service;

import com.simplecoding.michelin_back.admin.dto.NoticeDto;
import com.simplecoding.michelin_back.admin.entity.Admin;
import com.simplecoding.michelin_back.admin.entity.Notice;
import com.simplecoding.michelin_back.admin.repository.AdminRepository;
import com.simplecoding.michelin_back.admin.repository.NoticeRepository;
import com.simplecoding.michelin_back.common.CommonException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final AdminRepository adminRepository;
    private final AdminLogService adminLogService;

    public Page<NoticeDto.Response> getList(Pageable pageable) {
        return noticeRepository.findByDeletYnOrderByInsertTimeDesc("N", pageable)
                .map(this::toResponse);
    }

    public NoticeDto.Response getOne(Long noticeId) {
        return toResponse(findNotice(noticeId));
    }

    @Transactional
    public NoticeDto.Response create(Long memberId, NoticeDto.CreateRequest req) {
        Admin admin = adminRepository.findByMember_MemberId(memberId)
                .orElseThrow(() -> CommonException.forbidden("관리자 권한이 없습니다."));

        Notice notice = Notice.builder()
                .title(req.getTitle())
                .content(req.getContent())
                .writer(admin)
                .fixYn(req.getFixYn())
                .build();

        Notice saved = noticeRepository.save(notice);
        adminLogService.log(admin.getAdminId(), "NOTICE_CREATE", saved.getNoticeId(), req.getTitle());
        return toResponse(saved);
    }

    @Transactional
    public NoticeDto.Response update(Long memberId, Long noticeId, NoticeDto.UpdateRequest req) {
        Admin admin = adminRepository.findByMember_MemberId(memberId)
                .orElseThrow(() -> CommonException.forbidden("관리자 권한이 없습니다."));
        Notice notice = findNotice(noticeId);
        notice.update(req.getTitle(), req.getContent(), req.getFixYn() != null ? req.getFixYn() : notice.getFixYn());
        adminLogService.log(admin.getAdminId(), "NOTICE_UPDATE", noticeId, req.getTitle());
        return toResponse(notice);
    }

    @Transactional
    public void delete(Long memberId, Long noticeId) {
        Admin admin = adminRepository.findByMember_MemberId(memberId)
                .orElseThrow(() -> CommonException.forbidden("관리자 권한이 없습니다."));
        findNotice(noticeId).delete();
        adminLogService.log(admin.getAdminId(), "NOTICE_DELETE", noticeId, null);
    }

    private Notice findNotice(Long noticeId) {
        return noticeRepository.findById(noticeId)
                .filter(n -> "N".equals(n.getDeletYn()))
                .orElseThrow(() -> CommonException.notFound("공지사항을 찾을 수 없습니다."));
    }

    private NoticeDto.Response toResponse(Notice n) {
        return NoticeDto.Response.builder()
                .noticeId(n.getNoticeId())
                .title(n.getTitle())
                .content(n.getContent())
                .fixYn(n.getFixYn())
                .deletYn(n.getDeletYn())
                .writerId(n.getWriter() != null ? n.getWriter().getAdminId() : null)
                .insertTime(n.getInsertTime())
                .updateTime(n.getUpdateTime())
                .build();
    }
}
