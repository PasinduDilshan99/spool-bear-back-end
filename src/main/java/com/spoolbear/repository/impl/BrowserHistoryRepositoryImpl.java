package com.spoolbear.repository.impl;

import com.spoolbear.exception.DataAccessErrorExceptionHandler;
import com.spoolbear.exception.InsertFailedErrorExceptionHandler;
import com.spoolbear.exception.InternalServerErrorExceptionHandler;
import com.spoolbear.model.request.BrowsingHistoryRequest;
import com.spoolbear.model.request.InsertBrowserHistoryRequest;
import com.spoolbear.model.request.RemoveBrowserHistoryListRequest;
import com.spoolbear.model.request.RemoveBrowserHistoryRequest;
import com.spoolbear.model.response.BrowserHistoryResponse;
import com.spoolbear.queries.BrowserHistoryQueries;
import com.spoolbear.repository.BrowserHistoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Repository
public class BrowserHistoryRepositoryImpl implements BrowserHistoryRepository {

    private final static Logger LOGGER = LoggerFactory.getLogger(BrowserHistoryRepositoryImpl.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public BrowserHistoryRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addHistoryData(InsertBrowserHistoryRequest insertBrowserHistoryRequest, Long userId) {

        String INSERT_BROWSER_HISTORY_REQUEST = BrowserHistoryQueries.INSERT_BROWSER_HISTORY_REQUEST;

        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(INSERT_BROWSER_HISTORY_REQUEST);
                ps.setLong(1, insertBrowserHistoryRequest.getProductId());
                ps.setString(2, insertBrowserHistoryRequest.getName());
                ps.setLong(3, userId);
                ps.setString(4, "ACTIVE");
                return ps;
            });

        } catch (Exception e) {
            LOGGER.error("Error inserting browser history: {}", e.toString(), e);
            throw new InsertFailedErrorExceptionHandler("Something went wrong while inserting browser history");
        }
    }

    @Override
    public BrowserHistoryResponse getHistoryData(Long userId, BrowsingHistoryRequest request) {
        try {
            LOGGER.info("Fetching browser history for userId: {}", userId);

            // Base query
            String baseQuery = """
                FROM browser_history bh
                LEFT JOIN common_status cs ON bh.status_id = cs.id
                WHERE bh.user_id = ?
                AND cs.name = 'ACTIVE'
                """;

            List<Object> params = new ArrayList<>();
            params.add(userId);

            if (request.getFrom() != null) {
                baseQuery += " AND bh.created_at >= ?";
                params.add(new java.sql.Timestamp(request.getFrom().getTime()));
            }

            if (request.getTo() != null) {
                baseQuery += " AND bh.created_at <= ?";
                params.add(new java.sql.Timestamp(request.getTo().getTime()));
            }

            if (request.getNoOfLastDays() != null && request.getNoOfLastDays() > 0) {
                baseQuery += " AND bh.created_at >= NOW() - INTERVAL ? DAY";
                params.add(request.getNoOfLastDays());
            }

            // 1️⃣ Get total count
            String countQuery = "SELECT COUNT(*) " + baseQuery;
            Integer totalCount = jdbcTemplate.queryForObject(countQuery, params.toArray(), Integer.class);

            // 2️⃣ Fetch paginated history
            int pageSize = request.getPageSize() > 0 ? request.getPageSize() : 20;
            int offset = request.getPageNumber() > 0 ? (request.getPageNumber() - 1) * pageSize : 0;

            String dataQuery = "SELECT bh.id, bh.name, bh.product_id, bh.user_id, bh.created_at, cs.name AS status_name "
                    + baseQuery + " ORDER BY bh.created_at DESC LIMIT ? OFFSET ?";

            List<Object> dataParams = new ArrayList<>(params);
            dataParams.add(pageSize);
            dataParams.add(offset);

            List<BrowserHistoryResponse.HistoryResponse> history = jdbcTemplate.query(
                    dataQuery,
                    dataParams.toArray(),
                    (rs, rowNum) -> new BrowserHistoryResponse.HistoryResponse(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getLong("product_id"),
                            rs.getLong("user_id"),
                            rs.getTimestamp("created_at").toLocalDateTime(),
                            rs.getString("status_name")
                    )
            );

            // Return combined response
            return BrowserHistoryResponse.builder()
                    .totalCount(totalCount)
                    .history(history)
                    .build();

        } catch (DataAccessException ex) {
            LOGGER.error("Database error while fetching history: {}", ex.getMessage(), ex);
            throw new DataAccessErrorExceptionHandler("Failed to fetch history from database");
        } catch (Exception ex) {
            LOGGER.error("Unexpected error while fetching history: {}", ex.getMessage(), ex);
            throw new InternalServerErrorExceptionHandler("Unexpected error occurred while fetching history");
        }
    }

    @Override
    public void removeHistoryData(RemoveBrowserHistoryRequest request) {
        try {
            Integer terminatedStatusId = jdbcTemplate.queryForObject(
                    "SELECT id FROM common_status WHERE name = ?",
                    Integer.class,
                    "TERMINATED"
            );
            int rows = jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                        BrowserHistoryQueries.TERMINATE_BROWSER_HISTORY
                );

                ps.setInt(1, terminatedStatusId);
                ps.setLong(2, request.getHistoryDataId());

                return ps;
            });

            if (rows == 0) {
                throw new RuntimeException("No browser history found for given ID");
            }

        } catch (Exception e) {
            LOGGER.error("Error removing browser history: {}", e.toString(), e);
            throw new InsertFailedErrorExceptionHandler(
                    "Something went wrong while removing browser history"
            );
        }
    }

    @Override
    public void removeAllHistoryData(Long userId) {
        try {
            Integer terminatedStatusId = jdbcTemplate.queryForObject(
                    "SELECT id FROM common_status WHERE name = ?",
                    Integer.class,
                    "TERMINATED"
            );
            int rows = jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                        BrowserHistoryQueries.TERMINATE_ALL_BROWSER_HISTORY_BY_USER
                );

                ps.setInt(1, terminatedStatusId);
                ps.setLong(2, userId);

                return ps;
            });

            if (rows == 0) {
                LOGGER.warn("No browser history records found for userId: {}", userId);
            } else {
                LOGGER.info("Terminated {} browser history records for userId: {}", rows, userId);
            }

        } catch (Exception e) {
            LOGGER.error("Error removing all browser history for userId {}: {}", userId, e.toString(), e);
            throw new InsertFailedErrorExceptionHandler(
                    "Something went wrong while removing all browser history"
            );
        }
    }

    @Override
    public void removeHistoryDataList(RemoveBrowserHistoryListRequest request) {
        List<Long> ids = request.getHistoryDataIds();
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("History ID list cannot be empty");
        }

        try {
            Integer terminatedStatusId = jdbcTemplate.queryForObject(
                    "SELECT id FROM common_status WHERE name = ?",
                    Integer.class,
                    "TERMINATED"
            );
            String placeholders = String.join(",", Collections.nCopies(ids.size(), "?"));

            String sql = "UPDATE browser_history SET status_id = ? WHERE id IN (" + placeholders + ")";

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql);

                ps.setInt(1, terminatedStatusId);

                for (int i = 0; i < ids.size(); i++) {
                    ps.setLong(i + 2, ids.get(i));
                }

                return ps;
            });

        } catch (Exception e) {
            LOGGER.error("Error removing browser history list: {}", e.toString(), e);
            throw new InsertFailedErrorExceptionHandler(
                    "Something went wrong while removing browser history list"
            );
        }
    }
}
