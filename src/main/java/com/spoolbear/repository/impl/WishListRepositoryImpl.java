package com.spoolbear.repository.impl;

import com.spoolbear.exception.DataAccessErrorExceptionHandler;
import com.spoolbear.exception.InsertFailedErrorExceptionHandler;
import com.spoolbear.exception.InternalServerErrorExceptionHandler;
import com.spoolbear.exception.UpdateFailedErrorExceptionHandler;
import com.spoolbear.model.dto.ExistWishListDataDto;
import com.spoolbear.model.request.WishListInsertRequest;
import com.spoolbear.model.response.WishListResponse;
import com.spoolbear.queries.WishItemsQueries;
import com.spoolbear.repository.WishListRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
public class WishListRepositoryImpl implements WishListRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(WishListRepositoryImpl.class);

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedJdbc;

    @Autowired
    public WishListRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedJdbc = new NamedParameterJdbcTemplate(jdbcTemplate);
    }


    @Override
    public ExistWishListDataDto getExistingWishListData(Long userId, WishListInsertRequest wishListInsertRequest) {
        try {
            return jdbcTemplate.queryForObject(
                    WishItemsQueries.GET_EXISTING_WISHLIST_DATA,
                    new Object[]{userId, wishListInsertRequest.getProductId()},
                    (rs, rowNum) -> ExistWishListDataDto.builder()
                            .wishListId(rs.getLong("wishListId"))
                            .productId(rs.getLong("productId"))
                            .userId(rs.getLong("userId"))
                            .status(rs.getString("status"))
                            .createdAt(
                                    rs.getTimestamp("createdAt") != null
                                            ? rs.getTimestamp("createdAt").toLocalDateTime()
                                            : null
                            )
                            .updatedAt(
                                    rs.getTimestamp("updatedAt") != null
                                            ? rs.getTimestamp("updatedAt").toLocalDateTime()
                                            : null
                            )
                            .build()
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public Long addWishList(WishListInsertRequest wishListInsertRequest, Long userId) {
        String INSERT_WISH_DATA = WishItemsQueries.INSERT_WISH_DATA;
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();

            int rowsAffected = jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(INSERT_WISH_DATA, Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, userId);
                ps.setLong(2, wishListInsertRequest.getProductId());
                return ps;
            }, keyHolder);

            if (rowsAffected == 0) {
                throw new InsertFailedErrorExceptionHandler("No rows affected when inserting activity wish data");
            }
            Number generatedId = keyHolder.getKey();
            if (generatedId == null) {
                throw new InsertFailedErrorExceptionHandler("Failed to retrieve inserted activity wish ID");
            }
            Long activityWishId = generatedId.longValue();
            LOGGER.info("Inserted activity wish ID: {}", activityWishId);
            return activityWishId;
        } catch (InsertFailedErrorExceptionHandler e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.toString(), e);
            throw new InternalServerErrorExceptionHandler("Something went wrong");
        }
    }

    @Override
    public void addWishListHistory(WishListInsertRequest wishListInsertRequest,
                                   Long userId,
                                   Long wishListId,
                                   String status) {

        String INSERT_WISHLIST_HISTORY = WishItemsQueries.INSERT_WISHLIST_HISTORY;

        try {
            int rowsAffected = jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(INSERT_WISHLIST_HISTORY);

                ps.setLong(1, wishListId);   // wishlist_id
                ps.setString(2, status);     // status name (ACTIVE / REMOVED)

                return ps;
            });

            if (rowsAffected == 0) {
                throw new InsertFailedErrorExceptionHandler(
                        "No rows affected when inserting wishlist history"
                );
            }

            LOGGER.info("Inserted wishlist history for wishlist ID: {}", wishListId);

        } catch (InsertFailedErrorExceptionHandler e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error inserting wishlist history", e);
            throw new InternalServerErrorExceptionHandler("Something went wrong");
        }
    }

    @Override
    public void updateWishList(WishListInsertRequest wishListInsertRequest, Long userId, ExistWishListDataDto existActivityWishListDataDto) {
        try {
            if (existActivityWishListDataDto == null) {
                LOGGER.warn("Wishlist data not found");
                return;
            }
            String currentStatus = existActivityWishListDataDto.getStatus();
            Long wishListId = existActivityWishListDataDto.getWishListId();
            String newStatus;

            if ("ACTIVE".equalsIgnoreCase(currentStatus)) {
                newStatus = "INACTIVE";
            } else if ("INACTIVE".equalsIgnoreCase(currentStatus)) {
                newStatus = "ACTIVE";
            } else {
                LOGGER.warn("Status is neither ACTIVE nor INACTIVE. No update performed.");
                throw new UpdateFailedErrorExceptionHandler("Invalid wishlist status: " + currentStatus);
            }

            int rowsAffected = jdbcTemplate.update(
                    WishItemsQueries.UPDATE_WISHLIST_STATUS,
                    newStatus,
                    wishListId,
                    userId
            );
            if (rowsAffected == 0) {
                throw new UpdateFailedErrorExceptionHandler("Failed to update activity wishlist");
            }
            LOGGER.info("Wishlist status toggled successfully. Wishlist ID: {}, New Status: {}", wishListId, newStatus);
        } catch (UpdateFailedErrorExceptionHandler e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error updating wishlist status", e);
            throw new InternalServerErrorExceptionHandler("Something went wrong");
        }
    }

    @Override
    public List<WishListResponse> getWishListDetails(Long userId) {
        try {
            LOGGER.info("Fetching wishlist items for userId: {}", userId);

            Map<Long, WishListResponse> map = new LinkedHashMap<>();

            jdbcTemplate.query(
                    WishItemsQueries.GET_ALL_WISHLIST_ITEMS,
                    new Object[]{userId},
                    rs -> {
                        Long productId = rs.getLong("product_id");
                        WishListResponse item = map.get(productId);

                        if (item == null) {
                            item = WishListResponse.builder()
                                    .productId(productId)
                                    .productName(rs.getString("product_name"))
                                    .productDescription(rs.getString("description"))
                                    .productPrice(rs.getDouble("price"))
                                    .productDate(
                                            rs.getTimestamp("created_at") != null
                                                    ? rs.getTimestamp("created_at").toString()
                                                    : null
                                    )
                                    .productUrl(rs.getString("product_id"))
                                    .createdAt(rs.getTimestamp("created_at"))
                                    .status(rs.getString("status_name"))
                                    .productImages(new ArrayList<>())
                                    .build();

                            map.put(productId, item);
                        }
                        String imageUrl = rs.getString("image_url");
                        if (imageUrl != null) {
                            item.getProductImages().add(imageUrl);
                        }
                    }
            );

            return new ArrayList<>(map.values());

        } catch (DataAccessException ex) {
            LOGGER.error("DB error fetching wishlist items", ex);
            throw new DataAccessErrorExceptionHandler("Failed to fetch wishlist items");
        } catch (Exception ex) {
            LOGGER.error("Unexpected error fetching wishlist items", ex);
            throw new InternalServerErrorExceptionHandler("Unexpected error fetching wishlist items");
        }
    }

}
