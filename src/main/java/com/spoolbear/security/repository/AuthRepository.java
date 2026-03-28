package com.spoolbear.security.repository;

import com.spoolbear.model.request.SecretQuestionsUpdateRequest;
import com.spoolbear.model.response.SecretQuesionsAnswersDto;
import com.spoolbear.model.response.SecretQuestionResponse;
import com.spoolbear.security.model.RegisterUser;
import com.spoolbear.security.model.User;

import java.util.List;
import java.util.Optional;

public interface AuthRepository {
    void signup(RegisterUser registerUser);
    Optional<User> getUserByUsername(String username);

    List<SecretQuesionsAnswersDto> getSecretQuestionsAndAnswersByUserId(Long userId);

    String getPasswordByUsername(String username);

    List<SecretQuestionResponse> getActiveScretQuestions();

    void addSecretQuestions(Long userId, List<SecretQuestionsUpdateRequest.SecretQuestion> addQuestions);

    void updateSecretQuestions(Long userId, List<SecretQuestionsUpdateRequest.SecretQuestion> updateQuestions);

    void removeSecretQuestions(Long userId, List<Long> removeQuestionsIds);

    void changePassword(Long userId, String encode, String newPassword);

    void resetPassword(String username, String encode);

    List<SecretQuesionsAnswersDto> getSecretQuestionsAndAnswersByUsername(String username);
}
