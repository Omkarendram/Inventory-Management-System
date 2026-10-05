package com.inventory.repository;

import com.inventory.entity.ApprovalRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApprovalRequestRepository extends JpaRepository<ApprovalRequest, Long> {
    List<ApprovalRequest> findAllByOrderByCreatedAtDescIdDesc();

    List<ApprovalRequest> findByRequesterUserIdOrderByCreatedAtDescIdDesc(String requesterUserId);
}
