package com.simplecoding.michelin_back.admin.repository;

import com.simplecoding.michelin_back.admin.entity.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    Page<Notice> findByDeletYnOrderByInsertTimeDesc(String deletYn, Pageable pageable);
}
