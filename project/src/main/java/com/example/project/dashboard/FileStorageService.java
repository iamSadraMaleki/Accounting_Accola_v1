package com.example.project.dashboard;



import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {


    String storeFile(MultipartFile file);

}
