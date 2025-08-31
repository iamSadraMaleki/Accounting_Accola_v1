package com.example.project.request;

import com.example.project.cardmanagment.ResourceNotFoundException;
import com.example.project.dashboard.OperationNotAllowedException;
import com.example.project.signup.User;
import com.example.project.signup.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // <<< مهم

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class AdminRequestServiceImpl implements AdminRequestService {

    private static final Logger log = LoggerFactory.getLogger(AdminRequestServiceImpl.class);

    @Autowired
    private SellerRequestRepository sellerRequestRepository;

    @Autowired
    private UserRepository userRepository;


    private User getCurrentAdminUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("Admin user not found in getCurrentAdminUser for username: {}", username);
                    return new UsernameNotFoundException("کاربر ادمین یافت نشد: " + username);
                });
    }

    private SellerRequestDto mapToDto(SellerRequest request) {
        if (request == null) return null;
        SellerRequestDto dto = new SellerRequestDto();
        dto.setId(request.getId());
        dto.setRequestTime(request.getRequestTime());
        dto.setStatus(request.getStatus());
        dto.setRequestDetails(request.getRequestDetails());
        dto.setAdminNotes(request.getAdminNotes());
        dto.setResolutionTime(request.getResolutionTime());
        if (request.getUser() != null) {
            dto.setRequestingUserId(request.getUser().getId());
            dto.setRequestingUsername(request.getUser().getUsername());
        }
        if (request.getResolvedBy() != null) {
            dto.setResolverUsername(request.getResolvedBy().getUsername());
        }
        return dto;
    }



    @Override
    @Transactional(readOnly = true)
    public List<SellerRequestDto> getAllRequests() {
        log.info("Fetching all seller requests for admin");
        return sellerRequestRepository.findAllByOrderByRequestTimeDesc()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SellerRequestDto> getPendingRequests() {
        log.info("Fetching PENDING seller requests for admin");
        return sellerRequestRepository.findByStatusOrderByRequestTimeAsc(SellerRequest.RequestStatus.PENDING)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public SellerRequestDto getRequestById(Long requestId) {
        log.info("Fetching seller request with ID: {}", requestId);
        SellerRequest request = sellerRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("درخواستی با شناسه " + requestId + " یافت نشد."));
        return mapToDto(request);
    }

    @Override
    @Transactional
    public SellerRequestDto approveRequest(Long requestId, UpdateRequestStatusRequest requestDto) {
        User currentAdmin = getCurrentAdminUser();
        log.info("Admin {} attempting to APPROVE request ID: {}", currentAdmin.getUsername(), requestId);
        SellerRequest request = sellerRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("درخواستی با شناسه " + requestId + " یافت نشد."));
        if (request.getStatus() != SellerRequest.RequestStatus.PENDING) { throw new OperationNotAllowedException("این درخواست قبلاً بررسی شده."); }
        request.setStatus(SellerRequest.RequestStatus.APPROVED);
        request.setAdminNotes(requestDto.getAdminNotes());
        request.setResolvedBy(currentAdmin);
        request.setResolutionTime(LocalDateTime.now());
        User requestingUser = request.getUser();
        if (requestingUser != null) {
            log.info("Updating role for user {} to BUSINESS", requestingUser.getUsername());
            requestingUser.setRole("BUSINESS");
            userRepository.save(requestingUser);
        } else { log.error("Requesting user was null for request ID: {}.", requestId); }
        SellerRequest savedRequest = sellerRequestRepository.save(request);
        log.info("Request ID: {} successfully APPROVED by admin {}", requestId, currentAdmin.getUsername());
        return mapToDto(savedRequest);
    }

    @Override
    @Transactional
    public SellerRequestDto rejectRequest(Long requestId, UpdateRequestStatusRequest requestDto) {
        User currentAdmin = getCurrentAdminUser();
        log.info("Admin {} attempting to REJECT request ID: {}", currentAdmin.getUsername(), requestId);
        SellerRequest request = sellerRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("درخواستی با شناسه " + requestId + " یافت نشد."));
        if (request.getStatus() != SellerRequest.RequestStatus.PENDING) { throw new OperationNotAllowedException("این درخواست قبلاً بررسی شده."); }
        request.setStatus(SellerRequest.RequestStatus.REJECTED);
        request.setAdminNotes(requestDto.getAdminNotes());
        request.setResolvedBy(currentAdmin);
        request.setResolutionTime(LocalDateTime.now());
        SellerRequest savedRequest = sellerRequestRepository.save(request);
        log.info("Request ID: {} successfully REJECTED by admin {}", requestId, currentAdmin.getUsername());
        return mapToDto(savedRequest);
    }
}