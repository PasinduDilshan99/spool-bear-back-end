package com.spoolbear.security.service.impl;

import com.spoolbear.exception.*;
import com.spoolbear.model.request.PasswordChangeRequest;
import com.spoolbear.model.request.ResetPasswordRequest;
import com.spoolbear.model.request.SecretQuestionsUpdateRequest;
import com.spoolbear.model.request.UsernamePasswordValidationRequest;
import com.spoolbear.model.response.*;
import com.spoolbear.security.model.*;
import com.spoolbear.security.repository.AuthRepository;
import com.spoolbear.security.service.AuthService;
import com.spoolbear.security.service.JwtService;
import com.spoolbear.security.service.RefreshTokenService;
import com.spoolbear.service.CommonService;
import com.spoolbear.service.HelperService;
import com.spoolbear.util.CommonResponseMessages;
import com.spoolbear.validations.AuthValidationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final AuthRepository authRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final HelperService helperService;
    private final CommonService commonService;
    private final AuthValidationService authValidationService;

    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(12);

    @Autowired
    public AuthServiceImpl(AuthRepository authRepository,
                           AuthenticationManager authenticationManager,
                           JwtService jwtService,
                           RefreshTokenService refreshTokenService,
                           HelperService helperService,
                           CommonService commonService,
                           AuthValidationService authValidationService) {
        this.authRepository = authRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.helperService = helperService;
        this.refreshTokenService = refreshTokenService;
        this.commonService = commonService;
        this.authValidationService = authValidationService;
    }

    @Override
    public CommonResponse<String> signup(RegisterUser registerUser) {
        registerUser.setPassword(bCryptPasswordEncoder.encode(registerUser.getPassword()));
        try {
            authRepository.signup(registerUser);
            return new CommonResponse<>(
                    CommonResponseMessages.SUCCESSFULLY_INSERT_CODE,
                    CommonResponseMessages.SUCCESSFULLY_INSERT_STATUS,
                    CommonResponseMessages.SUCCESSFULLY_INSERT_MESSAGE,
                    "Successfully Signup",
                    Instant.now()
            );
        } catch (UserRegisterFailedErrorExceptionHandler | InternalServerErrorExceptionHandler e) {
            throw new UserRegisterFailedErrorExceptionHandler(e.getMessage());
        }
    }

    public CommonResponse<LoginResponse> login(LoginRequest loginRequest, HttpServletResponse response) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        if (authentication.isAuthenticated()) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            User domainUser = userDetails.getDomainUser();
            String accessToken = jwtService.generateAccessToken(domainUser);
            String refreshToken = jwtService.generateRefreshToken(domainUser);
            refreshTokenService.createRefreshToken(domainUser, refreshToken, jwtService.extractExpiration(refreshToken).toInstant());

            ResponseCookie accessCookie = jwtService.buildAccessTokenCookie(accessToken);
            ResponseCookie refreshCookie = jwtService.buildRefreshTokenCookie(refreshToken);
            response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
            response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

            LoginResponse loginSuccessful = LoginResponse.builder()
                    .message("Login successful")
                    .username(domainUser.getUsername())
                    .uniqueCode(helperService.generateUniqueCode())
                    .accessTokenExpiresAt(jwtService.extractExpiration(accessToken).toInstant())
                    .refreshTokenExpiresAt(jwtService.extractExpiration(refreshToken).toInstant())
                    .build();

            return new CommonResponse<>(
                    CommonResponseMessages.SUCCESSFULLY_LOGGING_CODE,
                    CommonResponseMessages.SUCCESSFULLY_LOGGING_STATUS,
                    CommonResponseMessages.SUCCESSFULLY_LOGGING_MESSAGE,
                    loginSuccessful,
                    Instant.now()
            );
        }
        throw new RuntimeException("Authentication failed");
    }

    @Override
    public CommonResponse<String> logout(HttpServletRequest request, HttpServletResponse response) {
        try {
            Optional<String> refreshTokenOpt = jwtService.resolveRefreshToken(request);
            refreshTokenOpt.ifPresent(refreshTokenService::revokeToken);
            ResponseCookie clearAccessCookie = jwtService.buildLogoutAccessTokenCookie();
            ResponseCookie clearRefreshCookie = jwtService.buildLogoutRefreshTokenCookie();
            response.addHeader(HttpHeaders.SET_COOKIE, clearAccessCookie.toString());
            response.addHeader(HttpHeaders.SET_COOKIE, clearRefreshCookie.toString());
            SecurityContextHolder.clearContext();
            return new CommonResponse<>(
                    CommonResponseMessages.SUCCESSFULLY_LOGOUT_CODE,
                    CommonResponseMessages.SUCCESSFULLY_LOGOUT_STATUS,
                    CommonResponseMessages.SUCCESSFULLY_LOGOUT_MESSAGE,
                    "Successfully Logout",
                    Instant.now()
            );
        } catch (Exception e) {
            return new CommonResponse<>(
                    CommonResponseMessages.UNSUCCESSFULLY_LOGOUT_CODE,
                    CommonResponseMessages.UNSUCCESSFULLY_LOGOUT_STATUS,
                    CommonResponseMessages.UNSUCCESSFULLY_LOGOUT_MESSAGE,
                    "Logout Unsuccessfully",
                    Instant.now()
            );
        }

    }

    @Override
    public CommonResponse<User> getCurrentUserProfile() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No authenticated user");
            }
            CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
            User user = principal.getDomainUser();
            user.setPassword(null);
            return new CommonResponse<>(
                    CommonResponseMessages.SUCCESSFULLY_RETRIEVE_CODE,
                    CommonResponseMessages.SUCCESSFULLY_RETRIEVE_STATUS,
                    CommonResponseMessages.SUCCESSFULLY_RETRIEVE_MESSAGE,
                    user,
                    Instant.now()
            );
        }catch (Exception e){
            throw new DataRetrieveFailedErrorExceptionHandler("No authenticated user");
        }

    }

    @Override
    public CommonResponse<ResetPasswordResponse> resetPassword(ResetPasswordRequest resetPasswordRequest) {
        LOGGER.info("Start reset password username : {}", resetPasswordRequest.getUsername());
        try {
            authValidationService.validateResetPasswordRequest(resetPasswordRequest);
            List<SecretQuesionsAnswersDto> secretQuesionsAnswersDtos = authRepository.getSecretQuestionsAndAnswersByUsername(resetPasswordRequest.getUsername());
            Boolean isValidQuestion1 = validateSecurityQuestionsAndAnswers(secretQuesionsAnswersDtos, resetPasswordRequest.getSecretQuestion1(), resetPasswordRequest.getSecretQuestion1Answer());
            LOGGER.info("Security question 1 validation result: {}", isValidQuestion1);
            Boolean isValidQuestion2 = validateSecurityQuestionsAndAnswers(secretQuesionsAnswersDtos, resetPasswordRequest.getSecretQuestion2(), resetPasswordRequest.getSecretQuestion2Answer());
            LOGGER.info("Security question 2 validation result: {}", isValidQuestion2);
            Boolean isValidQuestion3 = validateSecurityQuestionsAndAnswers(secretQuesionsAnswersDtos, resetPasswordRequest.getSecretQuestion3(), resetPasswordRequest.getSecretQuestion3Answer());
            LOGGER.info("Security question 3 validation result: {}", isValidQuestion3);

            if (isValidQuestion1 && isValidQuestion2 && isValidQuestion3) {
                authRepository.resetPassword(resetPasswordRequest.getUsername(), bCryptPasswordEncoder.encode(resetPasswordRequest.getNewPassword()));
                return new CommonResponse<>(
                        CommonResponseMessages.SUCCESSFULLY_UPDATE_CODE,
                        CommonResponseMessages.SUCCESSFULLY_UPDATE_STATUS,
                        CommonResponseMessages.SUCCESSFULLY_UPDATE_MESSAGE,
                        new ResetPasswordResponse("Password reset successfully"),
                        Instant.now());
            } else {
                return new CommonResponse<>(
                        CommonResponseMessages.UNSUCCESSFULLY_UPDATE_CODE,
                        CommonResponseMessages.UNSUCCESSFULLY_UPDATE_STATUS,
                        CommonResponseMessages.UNSUCCESSFULLY_UPDATE_MESSAGE,
                        new ResetPasswordResponse("Password reset failed"),
                        Instant.now());
            }


        } catch (DataNotFoundErrorExceptionHandler | DataAccessErrorExceptionHandler e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error occurred while reset password by username: {} , {}", resetPasswordRequest.getUsername(), e.getMessage(), e);
            throw new InternalServerErrorExceptionHandler("Error occurred while reset password by username: " + resetPasswordRequest.getUsername());
        } finally {
            LOGGER.info("End reset password by username : {} from repository", resetPasswordRequest.getUsername());
        }
    }

    @Override
    public CommonResponse<PasswordChangeResponse> changePassword(PasswordChangeRequest passwordChangeRequest) {
        LOGGER.info("Start change password username : {}", passwordChangeRequest.getUsername());
        try {
            authValidationService.validatePasswordChangeRequest(passwordChangeRequest);
            Long userId = commonService.getUserIdBySecurityContext();

            authRepository.changePassword(userId, bCryptPasswordEncoder.encode(passwordChangeRequest.getNewPassword()), passwordChangeRequest.getNewPassword());

            return new CommonResponse<>(
                    CommonResponseMessages.SUCCESSFULLY_UPDATE_CODE,
                    CommonResponseMessages.SUCCESSFULLY_UPDATE_STATUS,
                    CommonResponseMessages.SUCCESSFULLY_UPDATE_MESSAGE,
                    new PasswordChangeResponse("Password change successful."),
                    Instant.now());


        } catch (DataNotFoundErrorExceptionHandler | DataAccessErrorExceptionHandler e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error occurred while change password by username: {} , {}", passwordChangeRequest.getUsername(), e.getMessage(), e);
            throw new InternalServerErrorExceptionHandler("Error occurred while change password by username: " + passwordChangeRequest.getUsername());
        } finally {
            LOGGER.info("End change password by username : {} from repository", passwordChangeRequest.getUsername());
        }
    }

    @Override
    public CommonResponse<UpdateResponse> updateSecretQuestions(SecretQuestionsUpdateRequest secretQuestionsUpdateRequest) {
        LOGGER.info("Start update secret questions.");
        try {
            authValidationService.validateSecretQuestionsUpdateRequest(secretQuestionsUpdateRequest);
            Long userId = commonService.getUserIdBySecurityContext();

            authRepository.addSecretQuestions(userId, secretQuestionsUpdateRequest.getAddQuestions());
            authRepository.updateSecretQuestions(userId, secretQuestionsUpdateRequest.getUpdateQuestions());
            authRepository.removeSecretQuestions(userId, secretQuestionsUpdateRequest.getRemoveQuestionsIds());

            return new CommonResponse<>(
                    CommonResponseMessages.SUCCESSFULLY_UPDATE_CODE,
                    CommonResponseMessages.SUCCESSFULLY_UPDATE_STATUS,
                    CommonResponseMessages.SUCCESSFULLY_UPDATE_MESSAGE,
                    new UpdateResponse("Secret questions updated successfully.", userId),
                    Instant.now());


        } catch (DataNotFoundErrorExceptionHandler | DataAccessErrorExceptionHandler |
                 UpdateFailedErrorExceptionHandler e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error occurred while change secret questions, {}", e.getMessage(), e);
            throw new InternalServerErrorExceptionHandler("Error occurred while change secret questions.");
        } finally {
            LOGGER.info("End change secret questions from repository");
        }
    }

    @Override
    public CommonResponse<List<SecretQuestionResponse>> getActiveScretQuestions() {
        LOGGER.info("Start fetching active secret questions.");
        try {
            List<SecretQuestionResponse> secretQuestionResponses = authRepository.getActiveScretQuestions();
            return new CommonResponse<>(
                    CommonResponseMessages.SUCCESSFULLY_RETRIEVE_CODE,
                    CommonResponseMessages.SUCCESSFULLY_RETRIEVE_STATUS,
                    CommonResponseMessages.SUCCESSFULLY_RETRIEVE_MESSAGE,
                    secretQuestionResponses,
                    Instant.now());


        } catch (DataNotFoundErrorExceptionHandler | DataAccessErrorExceptionHandler e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error occurred while fetching secret questions, {}", e.getMessage(), e);
            throw new InternalServerErrorExceptionHandler("Error occurred while fetching secret questions.");
        } finally {
            LOGGER.info("End fetching secret questions from repository");
        }
    }

    @Override
    public CommonResponse<Boolean> usernamePasswordValidation(UsernamePasswordValidationRequest usernamePasswordValidationRequest) {
        LOGGER.info("Start validation username password username : {} .", usernamePasswordValidationRequest.getUsername());
        try {
            String encodedPassword = authRepository.getPasswordByUsername(usernamePasswordValidationRequest.getUsername());
            if (bCryptPasswordEncoder.matches(usernamePasswordValidationRequest.getPassword(), encodedPassword)) {
                return new CommonResponse<>(
                        CommonResponseMessages.SUCCESSFULLY_VALIDATE_CODE,
                        CommonResponseMessages.SUCCESSFULLY_VALIDATE_STATUS,
                        CommonResponseMessages.SUCCESSFULLY_VALIDATE_MESSAGE,
                        true,
                        Instant.now());
            } else {
                return new CommonResponse<>(
                        CommonResponseMessages.UNSUCCESSFULLY_VALIDATE_CODE,
                        CommonResponseMessages.UNSUCCESSFULLY_VALIDATE_STATUS,
                        CommonResponseMessages.UNSUCCESSFULLY_VALIDATE_MESSAGE,
                        false,
                        Instant.now());
            }

        } catch (DataNotFoundErrorExceptionHandler | DataAccessErrorExceptionHandler e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error occurred while validation username password, {}", e.getMessage(), e);
            throw new InternalServerErrorExceptionHandler("Error occurred while validation username password.");
        } finally {
            LOGGER.info("End validation username password from repository");
        }
    }

    @Override
    public CommonResponse<List<SecretQuesionsAnswersDto>> getActiveScretQuestionsByUser() {
        LOGGER.info("Start fetching secret questions");
        try {
            Long userId = commonService.getUserIdBySecurityContext();
            List<SecretQuesionsAnswersDto> secretQuesionsAnswersDtos = authRepository.getSecretQuestionsAndAnswersByUserId(userId);
            return new CommonResponse<>(
                    CommonResponseMessages.SUCCESSFULLY_RETRIEVE_CODE,
                    CommonResponseMessages.SUCCESSFULLY_RETRIEVE_STATUS,
                    CommonResponseMessages.SUCCESSFULLY_RETRIEVE_MESSAGE,
                    secretQuesionsAnswersDtos,
                    Instant.now());


        } catch (DataNotFoundErrorExceptionHandler | DataAccessErrorExceptionHandler e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error occurred while fetching secret questions : {} ", e.getMessage(), e);
            throw new InternalServerErrorExceptionHandler("Error occurred while fetching secret questions.");
        } finally {
            LOGGER.info("End fetching secret questions from repository");
        }
    }

    private Boolean validateSecurityQuestionsAndAnswers(
            List<SecretQuesionsAnswersDto> secretQuesionsAnswersDtos,
            Long questionId,
            String providedAnswer) {

        return secretQuesionsAnswersDtos.stream()
                .filter(dto -> Objects.equals(dto.getSecretQuestionId(), questionId))
                .anyMatch(dto -> {
                    String dbAnswer = dto.getAnswer();
                    LOGGER.info("Validating Security Question ID {}: providedAnswer='{}', dbAnswer='{}'",
                            questionId, providedAnswer, dbAnswer);
                    return Objects.equals(providedAnswer, dbAnswer);
                });
    }

}
