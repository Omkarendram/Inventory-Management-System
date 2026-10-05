package com.inventory.service;

import com.inventory.entity.ApprovalRequest;
import com.inventory.entity.Category;
import com.inventory.entity.Product;
import com.inventory.entity.RequestStatus;
import com.inventory.entity.RequestType;
import com.inventory.entity.User;
import com.inventory.repository.ApprovalRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApprovalRequestServiceTest {

    @Mock
    private ApprovalRequestRepository approvalRequestRepository;

    @Mock
    private ProductService productService;

    @Mock
    private CategoryService categoryService;

    @Mock
    private UserService userService;

    private ApprovalRequestService approvalRequestService;

    @BeforeEach
    void setUp() {
        approvalRequestService = new ApprovalRequestService(
            approvalRequestRepository,
            productService,
            categoryService,
            userService
        );
    }

    @Test
    void testCreateStockUpdateRequest() {
        User user = new User();
        user.setUserId("USER001");
        user.setUsername("Alice");

        Product product = new Product();
        product.setId(10L);
        product.setName("Widget");
        product.setQuantity(50);

        when(userService.findByUserId("USER001")).thenReturn(user);
        when(productService.getProductById(10L)).thenReturn(product);

        approvalRequestService.createStockUpdateRequest("USER001", 10L, 80);

        verify(approvalRequestRepository, times(1)).save(argThat(req ->
            req.getRequestType() == RequestType.STOCK_UPDATE &&
            req.getStatus() == RequestStatus.PENDING &&
            req.getProductId().equals(10L) &&
            req.getRequestedQuantity() == 80 &&
            "Alice".equals(req.getRequesterName())
        ));
    }

    @Test
    void testApproveStockUpdateRequest() {
        ApprovalRequest request = new ApprovalRequest();
        request.setId(1L);
        request.setRequestType(RequestType.STOCK_UPDATE);
        request.setStatus(RequestStatus.PENDING);
        request.setProductId(10L);
        request.setRequestedQuantity(100);

        when(approvalRequestRepository.findById(1L)).thenReturn(Optional.of(request));

        approvalRequestService.approveRequest(1L);

        verify(productService, times(1)).updateStock(10L, 100);
        assertEquals(RequestStatus.APPROVED, request.getStatus());
        verify(approvalRequestRepository, times(1)).save(request);
    }

    @Test
    void testApproveCategoryAddRequest() {
        ApprovalRequest request = new ApprovalRequest();
        request.setId(2L);
        request.setRequestType(RequestType.CATEGORY_ADD);
        request.setStatus(RequestStatus.PENDING);
        request.setCategoryName("Books");
        request.setCategoryDescription("Paperback and Hardcover");

        when(approvalRequestRepository.findById(2L)).thenReturn(Optional.of(request));

        approvalRequestService.approveRequest(2L);

        verify(categoryService, times(1)).saveCategory(argThat(cat ->
            "Books".equals(cat.getName()) && "Paperback and Hardcover".equals(cat.getDescription())
        ));
        assertEquals(RequestStatus.APPROVED, request.getStatus());
        verify(approvalRequestRepository, times(1)).save(request);
    }

    @Test
    void testRejectRequest() {
        ApprovalRequest request = new ApprovalRequest();
        request.setId(3L);
        request.setStatus(RequestStatus.PENDING);

        when(approvalRequestRepository.findById(3L)).thenReturn(Optional.of(request));

        approvalRequestService.rejectRequest(3L);

        assertEquals(RequestStatus.REJECTED, request.getStatus());
        verify(approvalRequestRepository, times(1)).save(request);
    }
}
