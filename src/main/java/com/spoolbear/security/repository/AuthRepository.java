package com.spoolbear.security.repository;

import com.spoolbear.security.model.RegisterUser;
import com.spoolbear.security.model.User;

import java.util.Optional;

public interface AuthRepository {
    void signup(RegisterUser registerUser);
    Optional<User> getUserByUsername(String username);
}
