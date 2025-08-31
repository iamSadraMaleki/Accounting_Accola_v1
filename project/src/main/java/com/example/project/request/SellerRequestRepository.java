package com.example.project.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SellerRequestRepository extends JpaRepository<SellerRequest, Long> {


    List<SellerRequest> findByStatusOrderByRequestTimeAsc(SellerRequest.RequestStatus status);


    List<SellerRequest> findAllByOrderByRequestTimeDesc();


    List<SellerRequest> findByUserIdOrderByRequestTimeDesc(Long userId);
}