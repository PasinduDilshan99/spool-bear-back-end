package com.spoolbear.repository.impl;

import com.spoolbear.exception.DataAccessErrorExceptionHandler;
import com.spoolbear.exception.DataNotFoundErrorExceptionHandler;
import com.spoolbear.exception.InternalServerErrorExceptionHandler;
import com.spoolbear.model.request.UserProfileAddressInsertRequest;
import com.spoolbear.model.request.UserProfileDetailsRequest;
import com.spoolbear.model.request.UserUpdateRequest;
import com.spoolbear.model.response.UserProfileDetailsResponse;
import com.spoolbear.model.response.UserProfileSidebarResponse;
import com.spoolbear.queries.UserProfileQueries;
import com.spoolbear.repository.UserProfileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserProfileRepositoryImpl implements UserProfileRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserProfileRepositoryImpl.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserProfileRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public Long getUserProfileAddressId(Long userId) {

        String GET_USER_PROFILE_ADDRESS_ID = UserProfileQueries.GET_USER_PROFILE_ADDRESS_ID;

        try {
            LOGGER.info("Fetching addressId for userId: {}", userId);

            Long addressId = jdbcTemplate.query(
                    GET_USER_PROFILE_ADDRESS_ID,
                    ps -> ps.setLong(1, userId),
                    rs -> rs.next() ? rs.getLong("address_id") : null
            );

            if (addressId == null || addressId == 0) {
                LOGGER.info("No addressId found for userId: {}", userId);
                return null;
            }

            LOGGER.info("Found addressId: {} for userId: {}", addressId, userId);
            return addressId;

        } catch (Exception ex) {
            LOGGER.error("Error fetching addressId for userId: {}. Returning null. Error: {}",
                    userId, ex.getMessage());
            return null;
        }
    }

    @Override
    public void updateUserProfileAddress(UserUpdateRequest updateRequest, Long addressId) {

        String UPDATE_USER_PROFILE_ADDRESS = UserProfileQueries.UPDATE_USER_PROFILE_ADDRESS;
        try {
            LOGGER.info("Executing query to update user profile address. AddressId: {}", addressId);

            int rowsAffected = jdbcTemplate.update(
                    UPDATE_USER_PROFILE_ADDRESS,
                    updateRequest.getAddressNumber(),
                    updateRequest.getAddressLine1(),
                    updateRequest.getAddressLine2(),
                    updateRequest.getCity(),
                    updateRequest.getDistrict(),
                    updateRequest.getProvince(),
                    updateRequest.getCountry(),
                    updateRequest.getPostalCode(),
                    addressId
            );
            if (rowsAffected == 0) {
                LOGGER.warn("No address record updated. AddressId: {}", addressId);
                throw new DataAccessErrorExceptionHandler("No address found for the given addressId");
            }
            LOGGER.info("Successfully updated user profile address. AddressId: {}, RowsAffected: {}", addressId, rowsAffected);
        } catch (DataAccessException ex) {
            LOGGER.error("Database error while updating user profile address. AddressId: {}, Error: {}", addressId, ex.getMessage(), ex);
            throw new DataAccessErrorExceptionHandler("Failed to update user profile address in database");

        } catch (Exception ex) {
            LOGGER.error("Unexpected error while updating user profile address. AddressId: {}, Error: {}", addressId, ex.getMessage(), ex);
            throw new InternalServerErrorExceptionHandler("Unexpected error occurred while updating user profile address");
        }
    }


    @Override
    public Long insertUserProfileAddress(UserProfileAddressInsertRequest request) {

        String INSERT_USER_PROFILE_ADDRESS = UserProfileQueries.INSERT_USER_PROFILE_ADDRESS;

        try {
            LOGGER.info("Executing query to insert user profile address. City: {}, PostalCode: {}",
                    request.getCity(), request.getPostalCode());

            KeyHolder keyHolder = new GeneratedKeyHolder();

            int rowsAffected = jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                        INSERT_USER_PROFILE_ADDRESS,
                        Statement.RETURN_GENERATED_KEYS
                );

                ps.setString(1, request.getAddressNumber());
                ps.setString(2, request.getAddressLine1());
                ps.setString(3, request.getAddressLine2());
                ps.setString(4, request.getCity());
                ps.setString(5, request.getDistrict());
                ps.setString(6, request.getProvince());
                ps.setString(7, request.getCountry());
                ps.setString(8, request.getPostalCode());

                return ps;
            }, keyHolder);

            if (rowsAffected == 0) {
                LOGGER.error("Failed to insert user profile address. No rows affected.");
                throw new DataAccessErrorExceptionHandler("Failed to insert user profile address");
            }

            Long generatedAddressId = keyHolder.getKey().longValue();

            LOGGER.info("Successfully inserted user profile address. AddressId: {}", generatedAddressId);

            return generatedAddressId;

        } catch (DataAccessException ex) {
            LOGGER.error("Database error while inserting user profile address. Error: {}", ex.getMessage(), ex);
            throw new DataAccessErrorExceptionHandler("Failed to insert user profile address in database");

        } catch (Exception ex) {
            LOGGER.error("Unexpected error while inserting user profile address. Error: {}", ex.getMessage(), ex);
            throw new InternalServerErrorExceptionHandler("Unexpected error occurred while inserting user profile address");
        }
    }

    @Override
    public void updateUserProfileDetails(UserUpdateRequest request, Long userId) {

        try {
            LOGGER.info("Updating user profile details for userId: {}", userId);
            int rowsAffected = jdbcTemplate.update(
                    UserProfileQueries.UPDATE_USER_PROFILE_DETAILS,
                    request.getFirstName(),
                    request.getMiddleName(),
                    request.getLastName(),
                    request.getAddressId(),
                    request.getNic(),
                    request.getGender(),
                    request.getEmail(),
                    request.getMobileNumber(),
                    request.getCountry(),
                    request.getDateOfBirth(),
                    request.getImageUrl(),
                    userId
            );

            if (rowsAffected == 0) {
                LOGGER.warn("User not found with userId: {}", userId);
                throw new DataNotFoundErrorExceptionHandler("User not found");
            }
            LOGGER.info("User profile updated successfully for userId: {}", userId);

        } catch (DataAccessException ex) {
            LOGGER.error("Database error while updating user profile: {}", ex.getMessage(), ex);
            throw new DataAccessErrorExceptionHandler("Failed to update user profile");
        } catch (Exception ex) {
            LOGGER.error("Unexpected error while updating user profile: {}", ex.getMessage(), ex);
            throw new InternalServerErrorExceptionHandler("Unexpected error occurred while updating user profile");
        }
    }

    @Override
    public List<UserProfileSidebarResponse> getUserProfileSideBar() {
        String GET_USER_PROFILE_SIDEBAR = UserProfileQueries.GET_USER_PROFILE_SIDEBAR;
        try {
            List<UserProfileSidebarResponse> flatList = jdbcTemplate.query(GET_USER_PROFILE_SIDEBAR, (rs, rowNum) -> {
                UserProfileSidebarResponse resp = new UserProfileSidebarResponse();
                resp.setId(rs.getInt("id"));
                resp.setParentId(rs.getObject("parent_id") != null ? rs.getInt("parent_id") : null);
                resp.setName(rs.getString("name"));
                resp.setDescription(rs.getString("description"));
                resp.setPrivilegeName(rs.getString("privilege_name"));
                resp.setStatus(rs.getString("status_name"));
                resp.setUrl(rs.getString("url"));
                return resp;
            });
            return buildSidebarTree(flatList);
        } catch (Exception ex) {
            LOGGER.error("Error fetching sidebar: {}", ex.getMessage(), ex);
            throw new InternalServerErrorExceptionHandler("Failed to fetch sidebar data");
        }
    }

    @Override
    public UserProfileDetailsResponse getUserProfileDetails(UserProfileDetailsRequest req) {
        String GET_USER_PROFILE_DETAILS = UserProfileQueries.GET_USER_PROFILE_DETAILS;
        try {
            LOGGER.info("Executing query to fetch user profile details...");
            return jdbcTemplate.queryForObject(
                    GET_USER_PROFILE_DETAILS,
                    new Object[]{req.getUserId()},
                    (rs, rowNum) -> {
                        UserProfileDetailsResponse response = new UserProfileDetailsResponse();
                        response.setUserId(rs.getInt("user_id"));
                        response.setUsername(rs.getString("username"));
                        response.setFirstName(rs.getString("first_name"));
                        response.setMiddleName(rs.getString("middle_name"));
                        response.setLastName(rs.getString("last_name"));
                        response.setNic(rs.getString("nic"));
                        response.setEmail(rs.getString("email"));
                        response.setMobileNumber(rs.getString("mobile_number"));
                        response.setDateOfBirth(rs.getString("date_of_birth"));
                        response.setImageUrl(rs.getString("image_url"));
                        response.setCreatedAt(rs.getString("created_at"));
                        response.setUpdatedAt(rs.getString("updated_at"));
                        response.setBenefitsPointsCount(rs.getInt("benefits_points_count"));
                        response.setAddressNumber(rs.getString("address_number"));
                        response.setAddressLine1(rs.getString("address_line1"));
                        response.setAddressLine2(rs.getString("address_line2"));
                        response.setCity(rs.getString("city"));
                        response.setDistrict(rs.getString("district"));
                        response.setProvince(rs.getString("province"));
                        response.setCountryName(rs.getString("country_name"));
                        response.setPostalCode(rs.getString("postal_code"));
                        response.setGender(rs.getString("gender"));
                        response.setUserType(rs.getString("user_type"));
                        response.setUserTypeDescription(rs.getString("user_type_description"));
                        response.setUserStatus(rs.getString("user_status"));
                        response.setUserStatusDescription(rs.getString("user_status_description"));
                        return response;
                    }
            );
        } catch (DataAccessException ex) {
            LOGGER.error("Database error while fetching user profile details: {}", ex.getMessage(), ex);
            throw new DataAccessErrorExceptionHandler("Failed to fetch user profile from database");
        } catch (Exception ex) {
            LOGGER.error("Unexpected error while fetching user profile details: {}", ex.getMessage(), ex);
            throw new InternalServerErrorExceptionHandler("Unexpected error occurred while fetching user profile");
        }
    }

    private List<UserProfileSidebarResponse> buildSidebarTree(List<UserProfileSidebarResponse> flatList) {
        Map<Integer, UserProfileSidebarResponse> map = new HashMap<>();
        flatList.forEach(item -> map.put(item.getId(), item));
        List<UserProfileSidebarResponse> rootItems = new ArrayList<>();
        for (UserProfileSidebarResponse item : flatList) {
            if (item.getParentId() == null) {
                rootItems.add(item);
            } else {
                UserProfileSidebarResponse parent = map.get(item.getParentId());
                if (parent.getChildren() == null) {
                    parent.setChildren(new ArrayList<>());
                }
                parent.getChildren().add(item);
            }
        }
        return rootItems;
    }

}
