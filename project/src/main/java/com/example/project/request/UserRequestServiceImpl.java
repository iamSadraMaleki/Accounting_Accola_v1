package com.example.project.request;




import com.example.project.signup.User;
import com.example.project.signup.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserRequestServiceImpl implements UserRequestService {

    private static final Logger log = LoggerFactory.getLogger(UserRequestServiceImpl.class);

    @Autowired private SellerRequestRepository sellerRequestRepository;
    @Autowired private UserRepository userRepository;

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("کاربر یافت نشد: " + username));
    }

    @Override
    @Transactional // <<< تراکنش مهم است
    public void submitSellerUpgradeRequest(SubmitSellerRequestDto requestDto, MultipartFile licenseFile) {
        User currentUser = getCurrentUser();
        log.info("User {} submitting seller upgrade request", currentUser.getUsername());


        String licenseFilePath = null;
        if (licenseFile != null && !licenseFile.isEmpty()) {
            try {

                licenseFilePath = "[PATH_FOR]_" + licenseFile.getOriginalFilename(); // این مسیر واقعی نیست!
                log.warn("Temporary: License file received: {}, pretending to save at: {}", licenseFile.getOriginalFilename(), licenseFilePath);


            } catch (Exception e) { // باید FileStorageException باشد
                log.error("Failed to store license file for user {}", currentUser.getUsername(), e);

                throw new FileStorageException("خطا در ذخیره فایل پروانه کسب: " + e.getMessage(), e);
            }
        } else {

            log.warn("No license file provided for seller request by user {}", currentUser.getUsername());
        }



        SellerRequest newRequest = new SellerRequest(currentUser, requestDto.getRequestDetails());
        newRequest.setStatus(SellerRequest.RequestStatus.PENDING); // وضعیت اولیه
        newRequest.getUser().setBusinessName(requestDto.getBusinessName());       // <<< فرض: فیلد businessName به User اضافه شده
        newRequest.getUser().setGuildCode(requestDto.getGuildCode());         // <<< فرض: فیلد guildCode به User اضافه شده
        newRequest.getUser().setUnionType(requestDto.getUnionType());         // <<< فرض: فیلد unionType به User اضافه شده
        newRequest.getUser().setActivityType(requestDto.getActivityType());     // <<< فرض: فیلد activityType به User اضافه شده
        newRequest.getUser().setBusinessUnitGrade(requestDto.getBusinessUnitGrade());// <<< فرض: فیلد businessUnitGrade به User اضافه شده
        newRequest.getUser().setWorkAddress(requestDto.getWorkAddress());     // <<< فرض: فیلد workAddress به User اضافه شده
        newRequest.getUser().setWorkPhone(requestDto.getWorkPhone());         // <<< فرض: فیلد workPhone به User اضافه شده
        newRequest.getUser().setBusinessLicensePath(licenseFilePath); // <<< فرض: فیلد businessLicensePath به User اضافه شده



        sellerRequestRepository.save(newRequest);
        log.info("Seller upgrade request saved with ID {} for user {}", newRequest.getId(), currentUser.getUsername());


        userRepository.save(currentUser);

    }
    @Override
    @Transactional(readOnly = true)
    public List<SellerRequestDto> getMyRequests() {
        User currentUser = getCurrentUser();
        log.info("Fetching request history for user: {}", currentUser.getUsername());


        List<SellerRequest> requests = sellerRequestRepository.findByUserIdOrderByRequestTimeDesc(currentUser.getId());


        return requests.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
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
        } else {

            dto.setRequestingUsername("کاربر نامشخص");
        }

        if (request.getResolvedBy() != null) {
            dto.setResolverUsername(request.getResolvedBy().getUsername());
        }
        return dto;
    }
}

