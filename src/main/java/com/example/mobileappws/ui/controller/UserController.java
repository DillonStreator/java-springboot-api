package com.example.mobileappws.ui.controller;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.example.mobileappws.exceptions.UserServiceException;
import com.example.mobileappws.service.AddressService;
import com.example.mobileappws.service.UserService;
import com.example.mobileappws.shared.dto.AddressDto;
import com.example.mobileappws.shared.dto.UserDto;
import com.example.mobileappws.ui.model.request.UserDetailsRequestModel;
import com.example.mobileappws.ui.model.response.AddressesRest;
import com.example.mobileappws.ui.model.response.ErrorMessages;
import com.example.mobileappws.ui.model.response.OperationStatusModel;
import com.example.mobileappws.ui.model.response.RequestOperationName;
import com.example.mobileappws.ui.model.response.RequestOperationStatus;
import com.example.mobileappws.ui.model.response.UserRest;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * UserController
 */
@RestController
@RequestMapping("users")
public class UserController {

    @Autowired
    UserService userService; //able to annotate with 'Autowired' because the UserServiceImpl class is annotated with 'Service'

    @Autowired
    AddressService addressesService;

    @GetMapping(
        path="/{id}",
        produces={MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE}
    )
    public UserRest getUser(@PathVariable String id)
    {
        UserRest returnValue = new UserRest();

        UserDto userDto = userService.getUserByUserId(id);
        BeanUtils.copyProperties(userDto, returnValue);
        
        return returnValue;
    }

    @GetMapping(
        produces={MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE}
    )
    public List<UserRest> getUsers(
        @RequestParam(value="page", defaultValue="0") int page,
        @RequestParam(value="limit", defaultValue="25") int limit
    )
    {
        List<UserRest> returnValue = new ArrayList<>();

        List<UserDto> users = userService.getUsers(page, limit);
        
        for (UserDto userDto : users) {
            UserRest userModel = new UserRest();
            BeanUtils.copyProperties(userDto, userModel);
            returnValue.add(userModel);
        }
        
        return returnValue;
    }

    @GetMapping(
        path="/{id}/addresses",
        produces={MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE}
    )
    public List<AddressesRest> getUserAddresses(@PathVariable String id)
    {
        List<AddressesRest> returnValue = new ArrayList<>();

        List<AddressDto> addressesDto = addressesService.getAddresses(id);
        if (addressesDto != null && !addressesDto.isEmpty())
        {
            Type listType = new TypeToken<List<AddressesRest>>() {}.getType();
            returnValue = new ModelMapper().map(addressesDto, listType);
        }

        return returnValue;
    }

    @GetMapping(
        path="/{userId}/addresses/{addressId}",
        produces={MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE}
    )
    public AddressesRest getUserAddress(@PathVariable String userId, @PathVariable String addressId)
    {

        AddressDto addressDto = addressesService.getAddress(addressId);
        AddressesRest returnValue = new ModelMapper().map(addressDto, AddressesRest.class);

        return returnValue;
    }

    @PostMapping(
        consumes={MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
        produces={MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE}
        )
    public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) throws Exception
    {
        UserRest returnValue = new UserRest();

        if(userDetails.getFirstName() == null || userDetails.getFirstName().isEmpty())
            throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());

        ModelMapper modelMapper = new ModelMapper();
        UserDto userDto = modelMapper.map(userDetails, UserDto.class);

        UserDto createdUser = userService.createUser(userDto);
        returnValue = modelMapper.map(createdUser, UserRest.class);
        
        return returnValue;
    }

    @PutMapping(
        path="/{id}",
        consumes={MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
        produces={MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE}
        )
    public UserRest updateUser(@RequestBody UserDetailsRequestModel userDetails, @PathVariable String id)
    {
        UserRest returnValue = new UserRest();

        if(userDetails.getFirstName() == null || userDetails.getFirstName().isEmpty())
            throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());

        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userDetails, userDto);

        UserDto updatedUser = userService.updateUser(id, userDto);
        BeanUtils.copyProperties(updatedUser, returnValue);
        
        return returnValue;
    }

    @DeleteMapping(
        path="/{id}",
        produces={MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE}
    )
    public OperationStatusModel deleteUser(@PathVariable String id)
    {
        OperationStatusModel returnValue = new OperationStatusModel();
        returnValue.setOperationName(RequestOperationName.DELETE.name());

        userService.deleteUser(id);
        
        returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
        return returnValue;
    }
}