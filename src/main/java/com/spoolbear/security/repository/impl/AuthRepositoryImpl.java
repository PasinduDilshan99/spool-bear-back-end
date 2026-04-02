package com.spoolbear.security.repository.impl;

import com.spoolbear.exception.DataAccessErrorExceptionHandler;
import com.spoolbear.exception.DataNotFoundErrorExceptionHandler;
import com.spoolbear.exception.InternalServerErrorExceptionHandler;
import com.spoolbear.exception.UserRegisterFailedErrorExceptionHandler;
import com.spoolbear.model.request.SecretQuestionsUpdateRequest;
import com.spoolbear.model.response.SecretQuesionsAnswersDto;
import com.spoolbear.model.response.SecretQuestionResponse;
import com.spoolbear.queries.AuthQueries;
import com.spoolbear.security.model.RegisterUser;
import com.spoolbear.security.model.User;
import com.spoolbear.security.repository.AuthRepository;
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
import java.util.List;
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

    @Override
    public List<SecretQuesionsAnswersDto> getSecretQuestionsAndAnswersByUsername(String username) {

        try {
            LOGGER.info("Fetching secret questions and answers for username: {}", username);

            return jdbcTemplate.query(
                    AuthQueries.GET_SECRET_QUESTIONS_AND_ANSWERS_BY_USERNAME,
                    new Object[]{username},
                    (rs, rowNum) -> SecretQuesionsAnswersDto.builder()
                            .secretQuestionId(rs.getLong("secret_question_id"))
                            .secretQuestion(rs.getString("question"))
                            .answer(rs.getString("secret_answer"))
                            .build()
            );

        } catch (DataAccessException ex) {
            LOGGER.error("Database error while fetching secret questions and answers: {}", ex.getMessage(), ex);
            throw new DataAccessErrorExceptionHandler("Failed to fetch secret questions and answers");
        } catch (Exception ex) {
            LOGGER.error("Unexpected error while fetching secret questions and answers: {}", ex.getMessage(), ex);
            throw new InternalServerErrorExceptionHandler("Unexpected error occurred while fetching secret questions and answers");
        }
    }


    @Override
    public void resetPassword(String username, String encodedPassword) {

        try {
            LOGGER.info("Resetting password for username: {}", username);

            int rowsAffected = jdbcTemplate.update(
                    AuthQueries.RESET_PASSWORD_BY_USERNAME,
                    encodedPassword,
                    username
            );

            if (rowsAffected == 0) {
                LOGGER.warn("No user found with username: {}", username);
                throw new DataNotFoundErrorExceptionHandler("User not found");
            }

            LOGGER.info("Password reset successful for username: {}", username);

        } catch (DataAccessException ex) {
            LOGGER.error("Database error while resetting password: {}", ex.getMessage(), ex);
            throw new DataAccessErrorExceptionHandler("Failed to reset password");
        } catch (Exception ex) {
            LOGGER.error("Unexpected error while resetting password: {}", ex.getMessage(), ex);
            throw new InternalServerErrorExceptionHandler("Unexpected error occurred while resetting password");
        }
    }

    @Override
    public void changePassword(Long userId, String encodedPassword, String newPassword) {

        try {
            LOGGER.info("Changing password for userId: {}", userId);

            int rowsAffected = jdbcTemplate.update(
                    AuthQueries.CHANGE_PASSWORD_BY_USER_ID,
                    encodedPassword,
                    userId
            );

            if (rowsAffected == 0) {
                LOGGER.warn("No user found with userId: {}", userId);
                throw new DataNotFoundErrorExceptionHandler("User not found");
            }

            LOGGER.info("Password changed successfully for userId: {}", userId);

        } catch (DataAccessException ex) {
            LOGGER.error("Database error while changing password: {}", ex.getMessage(), ex);
            throw new DataAccessErrorExceptionHandler("Failed to change password");
        } catch (Exception ex) {
            LOGGER.error("Unexpected error while changing password: {}", ex.getMessage(), ex);
            throw new InternalServerErrorExceptionHandler("Unexpected error occurred while changing password");
        }
    }

    @Override
    public void addSecretQuestions(Long userId, List<SecretQuestionsUpdateRequest.SecretQuestion> addQuestions) {

        try {
            LOGGER.info("Adding secret questions for userId: {}", userId);

            jdbcTemplate.batchUpdate(
                    AuthQueries.INSERT_USER_SECRET_QUESTION,
                    addQuestions,
                    addQuestions.size(),
                    (ps, question) -> {
                        ps.setLong(1, userId);
                        ps.setLong(2, question.getQuestion());
                        ps.setString(3, question.getAnswer()); // plain text for development
                    }
            );

            LOGGER.info("Secret questions added successfully");

        } catch (DataAccessException ex) {
            LOGGER.error("Error adding secret questions", ex);
            throw new DataAccessErrorExceptionHandler("Failed to add secret questions");
        }
    }


    @Override
    public void updateSecretQuestions(Long userId, List<SecretQuestionsUpdateRequest.SecretQuestion> updateQuestions) {

        try {
            LOGGER.info("Updating secret questions for userId: {}", userId);

            jdbcTemplate.batchUpdate(
                    AuthQueries.UPDATE_USER_SECRET_QUESTION,
                    updateQuestions,
                    updateQuestions.size(),
                    (ps, question) -> {
                        ps.setString(1, question.getAnswer());
                        ps.setLong(2, userId);
                        ps.setLong(3, question.getQuestion());
                    }
            );

            LOGGER.info("Secret questions updated successfully");

        } catch (DataAccessException ex) {
            LOGGER.error("Error updating secret questions", ex);
            throw new DataAccessErrorExceptionHandler("Failed to update secret questions");
        }
    }


    @Override
    public void removeSecretQuestions(Long userId, List<Long> removeQuestionsIds) {

        try {
            LOGGER.info("Removing secret questions for userId: {}", userId);

            jdbcTemplate.batchUpdate(
                    AuthQueries.REMOVE_USER_SECRET_QUESTION,
                    removeQuestionsIds,
                    removeQuestionsIds.size(),
                    (ps, questionId) -> {
                        ps.setLong(1, userId);
                        ps.setLong(2, questionId);
                    }
            );

            LOGGER.info("Secret questions removed successfully");

        } catch (DataAccessException ex) {
            LOGGER.error("Error removing secret questions", ex);
            throw new DataAccessErrorExceptionHandler("Failed to remove secret questions");
        }
    }

    @Override
    public List<SecretQuestionResponse> getActiveScretQuestions() {

        try {
            LOGGER.info("Fetching active secret questions");

            return jdbcTemplate.query(
                    AuthQueries.GET_ACTIVE_SECRET_QUESTIONS,
                    (rs, rowNum) -> SecretQuestionResponse.builder()
                            .questionId(rs.getLong("secret_question_id"))
                            .question(rs.getString("question"))
                            .build()
            );

        } catch (DataAccessException ex) {
            LOGGER.error("Database error while fetching secret questions: {}", ex.getMessage(), ex);
            throw new DataAccessErrorExceptionHandler("Failed to fetch secret questions");
        } catch (Exception ex) {
            LOGGER.error("Unexpected error while fetching secret questions: {}", ex.getMessage(), ex);
            throw new InternalServerErrorExceptionHandler("Unexpected error occurred while fetching secret questions");
        }
    }

    @Override
    public String getPasswordByUsername(String username) {

        try {
            return jdbcTemplate.queryForObject(
                    AuthQueries.GET_PASSWORD_BY_USERNAME,
                    new Object[]{username},
                    String.class
            );
        } catch (EmptyResultDataAccessException ex) {
            throw new DataNotFoundErrorExceptionHandler("User not found");
        } catch (DataAccessException ex) {
            throw new DataAccessErrorExceptionHandler("Database error occurred");
        }
    }

    @Override
    public List<SecretQuesionsAnswersDto> getSecretQuestionsAndAnswersByUserId(Long userId) {
        try {
            LOGGER.info("Fetching secret questions and answers for userId: {}", userId);

            return jdbcTemplate.query(
                    AuthQueries.GET_SECRET_QUESTIONS_AND_ANSWERS_BY_USER_ID,
                    new Object[]{userId},
                    (rs, rowNum) -> SecretQuesionsAnswersDto.builder()
                            .secretQuestionId(rs.getLong("secret_question_id"))
                            .secretQuestion(rs.getString("question"))
                            .answer(rs.getString("secret_answer"))
                            .build()
            );

        } catch (DataAccessException ex) {
            LOGGER.error("Database error while fetching secret questions and answers: {}", ex.getMessage(), ex);
            throw new DataAccessErrorExceptionHandler("Failed to fetch secret questions and answers");
        } catch (Exception ex) {
            LOGGER.error("Unexpected error while fetching secret questions and answers: {}", ex.getMessage(), ex);
            throw new InternalServerErrorExceptionHandler("Unexpected error occurred while fetching secret questions and answers");
        }
    }
}
