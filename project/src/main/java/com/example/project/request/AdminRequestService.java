package com.example.project.request;

import java.util.List;

public interface AdminRequestService {

    List<SellerRequestDto> getAllRequests();

    List<SellerRequestDto> getPendingRequests();

    SellerRequestDto getRequestById(Long requestId);

    SellerRequestDto approveRequest(Long requestId, UpdateRequestStatusRequest request);

    SellerRequestDto rejectRequest(Long requestId, UpdateRequestStatusRequest request);
}