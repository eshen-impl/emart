package com.chuwa.accountservice.dao;

import com.chuwa.accountservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
