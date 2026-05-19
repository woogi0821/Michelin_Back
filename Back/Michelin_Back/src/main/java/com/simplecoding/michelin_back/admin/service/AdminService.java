package com.simplecoding.michelin_back.admin.service;

import com.simplecoding.michelin_back.admin.entity.Admin;
import com.simplecoding.michelin_back.admin.repository.AdminRepository;
import com.simplecoding.michelin_back.common.CustomUserDetails;
import com.simplecoding.michelin_back.member.entity.Member;
import com.simplecoding.michelin_back.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;
    private final MemberRepository memberRepository;
    private final AdminLogService adminLogService;

    // 현재 로그인한 관리자 정보 조회
    @Transactional(readOnly = true)
    public Admin getAdminInfo(CustomUserDetails userDetails) {
        return getAdmin(userDetails.getMemberId());
    }

    // 회원 목록 조회
    @Transactional(readOnly = true)
    public Page<Member> getMembers(String status, Pageable pageable) {
        if (status == null || status.isBlank()) {
            return memberRepository.findAll(pageable);
        }
        return memberRepository.findByStatusOrderByInsertTimeDesc(status, pageable);
    }

    // 회원 상세 조회
    @Transactional(readOnly = true)
    public Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다."));
    }

    // 회원 정지
    @Transactional
    public void suspendMember(CustomUserDetails userDetails, Long memberId, LocalDate until) {
        Admin admin = getAdmin(userDetails.getMemberId());
        Member member = getMember(memberId);
        member.suspend(until);
        adminLogService.log(admin, "MEMBER_SUSPEND", "MEMBER", memberId,
                member.getName() + " / " + until + "까지");
    }

    // 회원 정지 해제
    @Transactional
    public void releaseMember(CustomUserDetails userDetails, Long memberId) {
        Admin admin = getAdmin(userDetails.getMemberId());
        Member member = getMember(memberId);
        member.releaseSuspension();
        adminLogService.log(admin, "MEMBER_RELEASE", "MEMBER", memberId, member.getName());
    }

    private Admin getAdmin(Long memberId) {
        return adminRepository.findByMember_MemberId(memberId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "관리자 권한이 없습니다."));
    }
}
