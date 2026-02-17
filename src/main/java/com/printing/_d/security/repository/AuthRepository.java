package com.printing._d.security.repository;

import com.printing._d.security.model.RegisterUser;
import com.printing._d.security.model.User;

import java.util.Optional;

public interface AuthRepository {
    void signup(RegisterUser registerUser);
    Optional<User> getUserByUsername(String username);
}
