package com.chuwa.accountservice.controller;

import com.chuwa.accountservice.model.UserSession;
import com.chuwa.accountservice.payload.AddressDTO;
import com.chuwa.accountservice.service.AddressService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/user/addresses")
public class AddressController {
    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;

    }

    @PostMapping
    public ResponseEntity<AddressDTO> addAddress(@Valid @RequestBody AddressDTO addressDTO, Principal principal) {
        UUID userId = UUID.fromString(principal.getName());
        AddressDTO createdAddress = addressService.addAddress(userId, addressDTO);
        return new ResponseEntity<>(createdAddress, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<AddressDTO>> getAddressesByUserId(Principal principal) {
        UUID userId = UUID.fromString(principal.getName());
        List<AddressDTO> addresses = addressService.getAddressesByUserId(userId);
        return new ResponseEntity<>(addresses, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<AddressDTO> updateAddress(@Valid @RequestBody AddressDTO addressDTO, Principal principal) {
        UUID userId = UUID.fromString(principal.getName());
        AddressDTO updatedAddress = addressService.updateAddress(userId, addressDTO);
        return new ResponseEntity<>(updatedAddress, HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<Void> removeAddress(@RequestBody AddressDTO addressDTO, Principal principal) {
        UUID userId = UUID.fromString(principal.getName());
        addressService.removeAddress(userId, addressDTO.getAddressId());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
