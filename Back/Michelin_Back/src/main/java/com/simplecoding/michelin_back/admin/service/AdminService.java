package com.simplecoding.michelin_back.admin.service;

import com.simplecoding.michelin_back.admin.dto.AdminDto;
import com.simplecoding.michelin_back.admin.entity.Admin;
import com.simplecoding.michelin_back.admin.repository.AdminRepository;
import com.simplecoding.michelin_back.common.CommonException;
import com.simplecoding.michelin_back.member.entity.Member;
import com.simplecoding.michelin_back.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {

    private final AdminRepository adminRepository;
    private final MemberRepository memberRepository;
    private final AdminLogService adminLogService;

    /** 관리자 권한 부여 */
    @Transactional
    public AdminDto.Response grant(Long requestAdminId, AdminDto.GrantRequest req) {
        Member member = memberRepository.findById(req.getMemberId())
                .orElseThrow(() -> CommonException.notFound("회원을 찾을 수 없습니다."));

        if (adminRepository.existsByMember_MemberId(req.getMemberId())) {
            throw CommonException.badRequest("이미 관리자 권한을 보유한 회원입니다.");
        }

        member.changeGrade("A");

        Admin admin = Admin.builder()
                .member(member)
                .adminRole(req.getAdminRole())
                .build();

        Admin saved = adminRepository.save(admin);
        adminLogService.log(requestAdminId, "ADMIN_GRANT", req.getMemberId(),
                "관리자 권한 부여: " + req.getAdminRole());

        return toResponse(saved);
    }

    /** 관리자 권한 회수 */
    @Transactional
    public void revoke(Long requestAdminId, Long adminId) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> CommonException.notFound("관리자를 찾을 수 없습니다."));
        Long memberId = admin.getMember().getMemberId();
        admin.getMember().changeGrade("N");
        adminRepository.delete(admin);
        adminLogService.log(requestAdminId, "ADMIN_REVOKE", memberId, "관리자 권한 회수");
    }

    public List<AdminDto.Response> getAll() {
        return adminRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private AdminDto.Response toResponse(Admin a) {
        return AdminDto.Response.builder()
                .adminId(a.getAdminId())
                .memberId(a.getMember().getMemberId())
                .loginId(a.getMember().getLoginId())
                .name(a.getMember().getName())
                .adminRole(a.getAdminRole())
                .build();
    }
}
