package com.simplecoding.michelin_back.admin.repository;

import com.simplecoding.michelin_back.admin.entity.PenaltyHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PenaltyHistoryRepository extends JpaRepository<PenaltyHistory, Long> {
    List<PenaltyHistory> findByMember_MemberIdOrderByInsertTimeDesc(Long memberId);
    Page<PenaltyHistory> findAllByOrderByInsertTimeDesc(Pageable pageable);
}
