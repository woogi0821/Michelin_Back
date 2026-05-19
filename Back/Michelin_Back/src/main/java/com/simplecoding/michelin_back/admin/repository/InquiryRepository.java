package com.simplecoding.michelin_back.admin.repository;

import com.simplecoding.michelin_back.admin.entity.Inquiry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
    Page<Inquiry> findByMember_MemberIdOrderByInsertTimeDesc(Long memberId, Pageable pageable);
    Page<Inquiry> findByStatusOrderByInsertTimeDesc(String status, Pageable pageable);
    Page<Inquiry> findAllByOrderByInsertTimeDesc(Pageable pageable);
    long countByStatus(String status);
}
