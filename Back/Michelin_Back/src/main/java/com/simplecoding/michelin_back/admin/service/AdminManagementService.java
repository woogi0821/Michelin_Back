package com.simplecoding.michelin_back.admin.service;

import com.simplecoding.michelin_back.admin.dto.AdminDto;
import com.simplecoding.michelin_back.admin.entity.Admin;
import com.simplecoding.michelin_back.admin.repository.AdminManagementRepository;
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
public class AdminManagementService {

    private final AdminManagementRepository adminManagementRepository;
    private final MemberRepository memberRepository;

    /** 관리자 목록 조회 */
    public List<AdminDto.AdminResponse> getAdminList() {
        return adminManagementRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /** 관리자 등록 */
    @Transactional
    public AdminDto.AdminResponse createAdmin(AdminDto.AdminCreateRequest req) {
        if (adminManagementRepository.existsByMemberId(req.getMemberId())) {
            throw CommonException.badRequest("이미 관리자로 등록된 회원입니다.");
        }
        memberRepository.findById(req.getMemberId())
                .orElseThrow(() -> CommonException.notFound("회원을 찾을 수 없습니다."));

        Admin admin = Admin.builder()
                .memberId(req.getMemberId())
                .adminRole(req.getAdminRole())
                .adminPart(req.getAdminPart())
                .build();

        return toResponse(adminManagementRepository.save(admin));
    }

    /** 관리자 삭제 */
    @Transactional
    public void deleteAdmin(Long adminId) {
        Admin admin = adminManagementRepository.findById(adminId)
                .orElseThrow(() -> CommonException.notFound("관리자를 찾을 수 없습니다."));
        adminManagementRepository.delete(admin);
    }

    /** 역할 변경 */
    @Transactional
    public void updateRole(Long adminId, String newRole) {
        Admin admin = adminManagementRepository.findById(adminId)
                .orElseThrow(() -> CommonException.notFound("관리자를 찾을 수 없습니다."));
        admin.changeRole(newRole);
    }

    /** 담당 파트 변경 */
    @Transactional
    public void updatePart(Long adminId, String newPart) {
        Admin admin = adminManagementRepository.findById(adminId)
                .orElseThrow(() -> CommonException.notFound("관리자를 찾을 수 없습니다."));
        admin.changePart(newPart);
    }

    private AdminDto.AdminResponse toResponse(Admin a) {
        String name = memberRepository.findById(a.getMemberId())
                .map(Member::getName)
                .orElse("알 수 없음");

        return AdminDto.AdminResponse.builder()
                .adminId(a.getAdminId())
                .memberId(a.getMemberId())
                .name(name)
                .adminRole(a.getAdminRole())
                .adminPart(a.getAdminPart())
                .build();
    }
}
