package com.chuwa.accountservice.model.compositekey;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Embeddable
public class AddressId implements Serializable {

    private UUID userId; // Foreign key to User
    private Long addressId; // Sequential ID for addresses (auto-generated)


    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        AddressId that = (AddressId) obj;
        return userId.equals(that.userId) && addressId.equals(that.addressId);
    }

    @Override
    public int hashCode() {
        return userId.hashCode() ^ addressId.hashCode();
    }
}

