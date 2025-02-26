package com.chuwa.accountservice.dao;

import com.chuwa.accountservice.model.Address;
import com.chuwa.accountservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findAddressesByUser(User user);
    Optional<Address> findByAddressIdAndUser(Long addressId, User user);


}
