package com.example.mobileappws.service;

import java.util.List;

import com.example.mobileappws.shared.dto.AddressDto;

/**
 * AddressService
 */
public interface AddressService {

    List<AddressDto> getAddresses(String userId);
    AddressDto getAddress(String addressId);
}