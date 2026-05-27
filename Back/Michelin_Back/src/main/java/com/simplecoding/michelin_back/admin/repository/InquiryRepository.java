package com.simplecoding.michelin_back.admin.repository;

import com.simplecoding.michelin_back.admin.entity.Inquiry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
    Page<Inquiry> findAllByOrderByCreatedAtDesc(Pageable pageable);
    Page<Inquiry> findByStatusOrderByCreatedAtDesc(String status, Pageable pageable);
}
