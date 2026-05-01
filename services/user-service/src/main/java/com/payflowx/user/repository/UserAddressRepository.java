package com.payflowx.user.repository;

import com.payflowx.user.entity.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserAddressRepository extends JpaRepository<UserAddress, UUID> {

    List<UserAddress> findByUserId(UUID userId);

    List<UserAddress> findByUserIdAndIsPrimaryTrue(UUID userId);
}