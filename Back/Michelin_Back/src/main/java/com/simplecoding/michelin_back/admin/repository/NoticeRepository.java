package com.simplecoding.michelin_back.admin.repository;

import com.simplecoding.michelin_back.admin.entity.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    // 삭제되지 않은 공지 목록 (최신순 페이징)
    Page<Notice> findByDeletYnOrderByFixYnDescInsertTimeDesc(String deletYn, Pageable pageable);

    // 고정 공지 조회
    Notice findTopByFixYnAndDeletYnOrderByInsertTimeDesc(String fixYn, String deletYn);
}
