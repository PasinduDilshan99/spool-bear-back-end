package com.printing._d.security.repository.impl;

import com.printing._d.exception.InternalServerErrorExceptionHandler;
import com.printing._d.exception.UserRegisterFailedErrorExceptionHandler;
import com.printing._d.security.model.RegisterUser;
import com.printing._d.security.model.User;
import com.printing._d.security.repository.AuthRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Repository
public class AuthRepositoryImpl implements AuthRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthRepositoryImpl.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public AuthRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void signup(RegisterUser registerUser) {
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            int rowsInserted = jdbcTemplate.update(con -> {
                PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO user (username, password, first_name, middle_name, last_name, email, mobile_number) " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS
                );
                ps.setString(1, registerUser.getUsername());
                ps.setString(2, registerUser.getPassword());
                ps.setString(3, registerUser.getFirstName());
                ps.setString(4, registerUser.getMiddleName());
                ps.setString(5, registerUser.getLastName());
                ps.setString(6, registerUser.getEmail());
                ps.setString(7, registerUser.getMobileNumber());
                return ps;
            }, keyHolder);

            if (rowsInserted == 0 || keyHolder.getKey() == null) {
                throw new UserRegisterFailedErrorExceptionHandler("User registration failed.");
            }

            registerUser.setUserId(keyHolder.getKey().longValue());
            Integer roleId = getRoleIdByName("ROLE_USER");
            if (roleId != null) {
                jdbcTemplate.update(
                        "INSERT INTO user_roles (user_id, role_id) VALUES (?, ?)",
                        registerUser.getUserId(),
                        roleId
                );

                registerUser.setRoles(fetchRoles(registerUser.getUserId()));
                registerUser.setPrivileges(fetchPrivileges(registerUser.getUserId()));
            }

        } catch (UserRegisterFailedErrorExceptionHandler ex) {
            throw ex;

        } catch (DataAccessException sqlEx) {
            throw new UserRegisterFailedErrorExceptionHandler("Database error: " + sqlEx.getMessage());

        } catch (Exception ex) {
            throw new InternalServerErrorExceptionHandler("Unexpected error: " + ex.getMessage());
        }
    }

    private Integer getRoleIdByName(String roleName) {
        try {
            return jdbcTemplate.queryForObject("SELECT id FROM roles WHERE name = ?", Integer.class, roleName);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    private Set<String> fetchRoles(Long userId) {
        String sql = """
                SELECT r.name FROM roles r 
                INNER JOIN user_roles ur ON r.id = ur.role_id 
                WHERE ur.user_id = ?
                """;
        return new HashSet<>(jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString("name"), userId));
    }

    private Set<String> fetchPrivileges(Long userId) {
        String sql = """
                SELECT DISTINCT p.name FROM privileges p 
                INNER JOIN role_privileges rp ON p.id = rp.privilege_id
                INNER JOIN user_roles ur ON rp.role_id = ur.role_id
                WHERE ur.user_id = ?
                """;
        return new HashSet<>(jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString("name"), userId));
    }

    public Optional<User> getUserByUsername(String username) {
        String sql = "SELECT * FROM user WHERE username = ?";

        try {
            User user = jdbcTemplate.queryForObject(sql, new Object[]{username}, (rs, rowNum) -> User.builder()
                    .id(rs.getLong("user_id"))
                    .username(rs.getString("username"))
                    .password(rs.getString("password"))
                    .firstName(rs.getString("first_name"))
                    .middleName(rs.getString("middle_name"))
                    .lastName(rs.getString("last_name"))
                    .email(rs.getString("email"))
                    .mobileNumber(rs.getString("mobile_number"))
                    .imageUrl(rs.getString("image_url"))
                    .build());

            if (user != null) {
                user.setRoles(fetchRoles(user.getId()));
                user.setPrivileges(fetchPrivileges(user.getId()));
            }
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

}
