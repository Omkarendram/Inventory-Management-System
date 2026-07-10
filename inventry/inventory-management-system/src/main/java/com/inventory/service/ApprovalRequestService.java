package com.inventory.service;

import com.inventory.entity.ApprovalRequest;
import com.inventory.entity.Category;
import com.inventory.entity.Product;
import com.inventory.entity.RequestStatus;
import com.inventory.entity.RequestType;
import com.inventory.entity.User;
import com.inventory.repository.ApprovalRequestRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApprovalRequestService {

    private final ApprovalRequestRepository approvalRequestRepository;
    private final ProductService productService;
    private final CategoryService categoryService;
    private final UserService userService;

    public ApprovalRequestService(
        ApprovalRequestRepository approvalRequestRepository,
        ProductService productService,
        CategoryService categoryService,
        UserService userService
    ) {
        this.approvalRequestRepository = approvalRequestRepository;
        this.productService = productService;
        this.categoryService = categoryService;
        this.userService = userService;
    }

    public List<ApprovalRequest> getAllRequests() {
        return approvalRequestRepository.findAllByOrderByCreatedAtDescIdDesc();
    }

    public List<ApprovalRequest> getRequestsForUser(String requesterUserId) {
        return approvalRequestRepository.findByRequesterUserIdOrderByCreatedAtDescIdDesc(requesterUserId);
    }

    public void createStockUpdateRequest(String requesterUserId, Long productId, int requestedQuantity) {
        Product product = productService.getProductById(productId);
        User user = userService.findByUserId(requesterUserId);

        ApprovalRequest request = new ApprovalRequest();
        request.setRequesterUserId(requesterUserId);
        request.setRequesterName(user != null ? user.getUsername() : requesterUserId);
        request.setRequestType(RequestType.STOCK_UPDATE);
        request.setStatus(RequestStatus.PENDING);
        request.setProductId(productId);
        request.setProductName(product != null ? product.getName() : "");
        request.setCurrentQuantity(product != null ? product.getQuantity() : 0);
        request.setRequestedQuantity(requestedQuantity);
        request.setCreatedAt(LocalDateTime.now());
        approvalRequestRepository.save(request);
    }

    public void createCategoryAddRequest(String requesterUserId, String categoryName, String categoryDescription) {
        User user = userService.findByUserId(requesterUserId);

        ApprovalRequest request = new ApprovalRequest();
        request.setRequesterUserId(requesterUserId);
        request.setRequesterName(user != null ? user.getUsername() : requesterUserId);
        request.setRequestType(RequestType.CATEGORY_ADD);
        request.setStatus(RequestStatus.PENDING);
        request.setCategoryName(categoryName);
        request.setCategoryDescription(categoryDescription);
        request.setCreatedAt(LocalDateTime.now());
        approvalRequestRepository.save(request);
    }

    public void approveRequest(Long id) {
        ApprovalRequest request = approvalRequestRepository.findById(id).orElseThrow();
        if (request.getStatus() != RequestStatus.PENDING) {
            return;
        }

        if (request.getRequestType() == RequestType.STOCK_UPDATE && request.getProductId() != null && request.getRequestedQuantity() != null) {
            productService.updateStock(request.getProductId(), request.getRequestedQuantity());
        }

        if (request.getRequestType() == RequestType.CATEGORY_ADD && request.getCategoryName() != null && !request.getCategoryName().isBlank()) {
            Category category = new Category();
            category.setName(request.getCategoryName().trim());
            category.setDescription(request.getCategoryDescription() == null ? "" : request.getCategoryDescription().trim());
            categoryService.saveCategory(category);
        }

        request.setStatus(RequestStatus.APPROVED);
        approvalRequestRepository.save(request);
    }

    public void rejectRequest(Long id) {
        ApprovalRequest request = approvalRequestRepository.findById(id).orElseThrow();
        if (request.getStatus() == RequestStatus.PENDING) {
            request.setStatus(RequestStatus.REJECTED);
            approvalRequestRepository.save(request);
        }
    }

    public int approveAllPendingRequests() {
        List<ApprovalRequest> pendingRequests = approvalRequestRepository.findAllByOrderByCreatedAtDescIdDesc().stream()
            .filter(request -> request.getStatus() == RequestStatus.PENDING)
            .collect(Collectors.toList());

        pendingRequests.forEach(request -> approveRequest(request.getId()));
        return pendingRequests.size();
    }

    public int rejectAllPendingRequests() {
        List<ApprovalRequest> pendingRequests = approvalRequestRepository.findAllByOrderByCreatedAtDescIdDesc().stream()
            .filter(request -> request.getStatus() == RequestStatus.PENDING)
            .collect(Collectors.toList());

        pendingRequests.forEach(request -> rejectRequest(request.getId()));
        return pendingRequests.size();
    }

}
