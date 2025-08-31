package com.example.project.request;


import org.springframework.web.multipart.MultipartFile; // برای فایل آپلود

import java.util.List;

public interface UserRequestService {


    void submitSellerUpgradeRequest(SubmitSellerRequestDto requestDto, MultipartFile licenseFile);

    List<SellerRequestDto> getMyRequests();
}
