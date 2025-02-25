package com.chuwa.accountservice.service.impl;

import com.chuwa.accountservice.dao.UserRepository;
import com.chuwa.accountservice.exception.DuplicateResourceException;
import com.chuwa.accountservice.exception.ResourceNotFoundException;
import com.chuwa.accountservice.model.User;
import com.chuwa.accountservice.payload.RegisterUserDTO;
import com.chuwa.accountservice.payload.UserInfoDTO;
import com.chuwa.accountservice.service.AccountService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AccountServiceImpl implements AccountService {

    private final UserRepository userRepository;

    public AccountServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserInfoDTO createAccount(RegisterUserDTO registerUserDTO) {
        String email = registerUserDTO.getEmail();
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("User with email " + email + " already exists.");
        }

        User user = new User();
        user.setEmail(email);
        user.setUsername(registerUserDTO.getUsername());

        //TODO: password encoder

        User newUser = userRepository.save(user);
        return convertToUserInfoDTO(newUser);
    }

    @Override
    public UserInfoDTO updateUserInfo(UUID userId, UserInfoDTO userInfoDTO) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        existingUser.setEmail(userInfoDTO.getEmail());
        existingUser.setUsername(userInfoDTO.getUsername());

        User updatedUser = userRepository.save(existingUser);
        return convertToUserInfoDTO(updatedUser);
    }

    @Override
    public UserInfoDTO updateUserPassword(UUID userId, String password) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        //TODO: password encoder

        User updatedUser = userRepository.save(existingUser);
        return convertToUserInfoDTO(updatedUser);
    }

    @Override
    public UserInfoDTO getAccountById(UUID userId) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return convertToUserInfoDTO(existingUser);
    }

    @Override
    public Page<UserInfoDTO> getAllAccounts(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);
        return users.map(this::convertToUserInfoDTO);
    }

    private UserInfoDTO convertToUserInfoDTO(User user) {
        return new UserInfoDTO(user.getEmail(), user.getUsername());
    }
}
