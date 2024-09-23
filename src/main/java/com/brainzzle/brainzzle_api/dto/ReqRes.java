package com.brainzzle.brainzzle_api.dto;

import com.brainzzle.brainzzle_api.entities.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NonNull;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReqRes {

    private int statusCode;
    private String error;
    private String message;
    private String token;
    private String refreshToken;
    private String expirationTime;
    private String firstName;
    private String lastName;
    private String role;
    private String email;
    private String password;
    private User user;
    private List<User> userList;
}
