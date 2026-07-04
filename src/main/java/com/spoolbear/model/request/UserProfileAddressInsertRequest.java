package com.spoolbear.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class UserProfileAddressInsertRequest {
    private String addressNumber;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String district;
    private String province;
    private String postalCode;
    private String country;
}
