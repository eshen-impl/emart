package com.chuwa.accountservice.dao;

import com.chuwa.accountservice.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;



public interface RoleRepository extends JpaRepository<Role, Long> {
}
