package com.chuwa.accountservice.dao;

import com.chuwa.accountservice.model.Address;
import com.chuwa.accountservice.model.User;
import com.chuwa.accountservice.model.compositekey.AddressId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface AddressRepository extends JpaRepository<Address, AddressId> {
    List<Address> findAddressesByUser(User user);


}
