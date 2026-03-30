package com.spoolbear.repository.impl;

import com.spoolbear.exception.DataAccessErrorExceptionHandler;
import com.spoolbear.exception.DataNotFoundErrorExceptionHandler;
import com.spoolbear.exception.InternalServerErrorExceptionHandler;
import com.spoolbear.model.dto.OrderInsertRequestDto;
import com.spoolbear.model.dto.OrderMainDetailsDto;
import com.spoolbear.model.dto.PrintingOrderInsertRequestDto;
import com.spoolbear.model.request.ProductOrderInsertRequest;
import com.spoolbear.model.response.OrderResponse;
import com.spoolbear.model.response.ProductsCartResponse;
import com.spoolbear.queries.CartQueries;
import com.spoolbear.queries.OrderQueries;
import com.spoolbear.repository.OrderRepository;
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
import java.sql.Types;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class OrderRepositoryImpl implements OrderRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderRepositoryImpl.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public OrderRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<OrderMainDetailsDto> getOrderMainDetailsByUserId(Long userId) {
        try {
            return jdbcTemplate.query(OrderQueries.GET_ORDER_MAIN_DETAILS_BY_USER_ID,
                    new Object[]{userId},
                    (rs, rowNum) -> OrderMainDetailsDto.builder()
                            .orderId(rs.getLong("order_id"))
                            .userId(rs.getLong("user_id"))
                            .totalAmount(rs.getDouble("total_amount"))
                            .orderStatus(rs.getString("order_status"))
                            .orderStatus(rs.getString("payment_status"))
                            .status(rs.getString("status"))
                            .createdDate(rs.getTimestamp("created_at"))
                            .updatedDate(rs.getTimestamp("updated_at"))
                            .orderType(rs.getString("order_type"))
                            .build()
            );
        } catch (EmptyResultDataAccessException ex) {
            throw new DataNotFoundErrorExceptionHandler("No orders found for userId: " + userId);
        } catch (DataAccessException ex) {
            throw new DataAccessErrorExceptionHandler("Database error while fetching orders");
        }
    }

    @Override
    public List<OrderResponse.products> getProductsDetailsByOrderIdList(List<Long> productOrderIds) {
        if (productOrderIds.isEmpty()) return Collections.emptyList();

        // Build dynamic IN clause
        String inSql = productOrderIds.stream().map(id -> "?").collect(Collectors.joining(","));
        String query = OrderQueries.GET_PRODUCTS_DETAILS_BY_ORDER_ID_LIST_PREFIX + inSql + ")";

        try {
            return jdbcTemplate.query(query,
                    productOrderIds.toArray(),
                    (rs, rowNum) -> {
                        OrderResponse.Images image = null;
                        if (rs.getString("image_id") != null) {
                            image = OrderResponse.Images.builder()
                                    .imageId(rs.getString("image_id"))
                                    .imageUrl(rs.getString("image_url"))
                                    .isPrimary(rs.getBoolean("is_primary"))
                                    .imageStatus(rs.getString("image_status"))
                                    .createdAt(rs.getTimestamp("image_created_at"))
                                    .build();
                        }

                        return OrderResponse.products.builder()
                                .orderId(rs.getLong("order_id"))
                                .productId(rs.getLong("product_id"))
                                .productName(rs.getString("product_name"))
                                .productDescription(rs.getString("product_description"))
                                .productType(rs.getString("product_type"))
                                .material(rs.getString("material"))
                                .quantity(rs.getInt("quantity"))
                                .color(rs.getString("color_name"))
                                .imagesList(image != null ? List.of(image) : new ArrayList<>())
                                .build();
                    });
        } catch (DataAccessException ex) {
            throw new DataAccessErrorExceptionHandler("Database error while fetching product order details");
        }
    }

    @Override
    public Long addPrintingOrder(PrintingOrderInsertRequestDto dto) {
        try {
            LOGGER.info("add printing order for userId: {}", dto.getUserId());


            String INSERT_PRINTING_ORDER_SQL = """
                        INSERT INTO printing_orders (
                            order_id,
                            product_id,
                            custom_text,
                            description,
                            size,
                            color,
                            material_id,
                            quantity,
                            price,
                            status,
                            created_by
                        ) VALUES (?, ?, ?, ?, ?, ?, (SELECT m.material_id FROM materials m WHERE m.name = ?), ?, ?, ?, ?)
                    """;

            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                        INSERT_PRINTING_ORDER_SQL,
                        Statement.RETURN_GENERATED_KEYS
                );

                int index = 1;

                ps.setLong(index++, dto.getOrderId());
                ps.setObject(index++, dto.getProductId()); // product_id (nullable)
                ps.setString(index++, dto.getCustomText());
                ps.setString(index++, dto.getDescription());
                ps.setString(index++, dto.getSize());
                ps.setString(index++, dto.getColor());
                ps.setObject(index++, dto.getMateriel()); // material_id (nullable)
                ps.setInt(index++, dto.getQuantity());
                ps.setObject(index++, null); // price (nullable or calculate)
                ps.setInt(index++, 1); // status
                ps.setLong(index++, dto.getUserId()); // created_by

                return ps;
            }, keyHolder);

            Long printingOrderId = keyHolder.getKey().longValue();
            LOGGER.info("Printing order created with id: {}", printingOrderId);

            return printingOrderId;

        } catch (DataAccessException ex) {
            LOGGER.error("Database error while add printing order for userId {}: {}",
                    dto.getUserId(), ex.getMessage(), ex);
            throw new DataAccessErrorExceptionHandler("Failed to create printing order");
        } catch (Exception ex) {
            LOGGER.error("Unexpected error while add printing order for userId {}: {}",
                    dto.getUserId(), ex.getMessage(), ex);
            throw new InternalServerErrorExceptionHandler("Unexpected error occurred while add printing order");
        }
    }

    @Override
    public void addPrintingOrderFile(String fileName, String fileUrl, Long printOrderId, Long userId) {
        try {
            LOGGER.info("Adding file for printingOrderId: {}", printOrderId);

            String INSERT_ORDER_FILE_SQL = """
                        INSERT INTO order_files (
                            printing_order_id,
                            file_url,
                            file_name,
                            created_by
                        ) VALUES (?, ?, ?, ?)
                    """;

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(INSERT_ORDER_FILE_SQL);

                int index = 1;

                ps.setLong(index++, printOrderId);
                ps.setString(index++, fileUrl);
                ps.setString(index++, fileName);
                ps.setLong(index++, userId);

                return ps;
            });

            LOGGER.info("File added successfully for printingOrderId: {}", printOrderId);

        } catch (DataAccessException ex) {
            LOGGER.error("Database error while adding file for printingOrderId {}: {}",
                    printOrderId, ex.getMessage(), ex);
            throw new DataAccessErrorExceptionHandler("Failed to add printing order file");
        } catch (Exception ex) {
            LOGGER.error("Unexpected error while adding file for printingOrderId {}: {}",
                    printOrderId, ex.getMessage(), ex);
            throw new InternalServerErrorExceptionHandler("Unexpected error occurred while adding printing order file");
        }
    }

    @Override
    public Long addOrder(OrderInsertRequestDto dto) {
        try {
            LOGGER.info("Creating order for userId: {}", dto.getUserId());

            String INSERT_ORDER_SQL = """
                        INSERT INTO orders (
                            user_id,
                            total_amount,
                            order_status,
                            status,
                            created_by,
                            order_type,
                            payment_status
                        ) VALUES (?, ?, ?, ?, ?, ?, ?)
                    """;

            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                        INSERT_ORDER_SQL,
                        Statement.RETURN_GENERATED_KEYS
                );

                int index = 1;

                ps.setLong(index++, dto.getUserId()); // user_id
                ps.setObject(index++, dto.getTotalAmount()); // total_amount
                ps.setString(index++, dto.getOrderStatus()); // order_status
                ps.setInt(index++, dto.getStatus()); // status
                ps.setLong(index++, dto.getUserId()); // created_by
                ps.setString(index++, dto.getOrderType()); // order_type
                ps.setString(index++, dto.getPaymentStatus()); // payment_status

                return ps;
            }, keyHolder);

            Long orderId = keyHolder.getKey().longValue();
            LOGGER.info("Order created successfully with id: {}", orderId);

            return orderId;

        } catch (DataAccessException ex) {
            LOGGER.error("Database error while creating order for userId {}: {}",
                    dto.getUserId(), ex.getMessage(), ex);
            throw new DataAccessErrorExceptionHandler("Failed to create order");
        } catch (Exception ex) {
            LOGGER.error("Unexpected error while creating order for userId {}: {}",
                    dto.getUserId(), ex.getMessage(), ex);
            throw new InternalServerErrorExceptionHandler("Unexpected error occurred while creating order");
        }
    }

    @Override
    public boolean addProductOrder(ProductOrderInsertRequest.OrderProducts orderProducts,
                                   Long orderId,
                                   Long userId) {

        Long colorId = null;
        if (orderProducts.getColor() != null) {
            try {
                colorId = jdbcTemplate.queryForObject(
                        CartQueries.GET_COLOR_ID_BY_NAME,
                        new Object[]{orderProducts.getColor()},
                        Long.class
                );
            } catch (EmptyResultDataAccessException ex) {
                LOGGER.warn("Color not found: {}", orderProducts.getColor());
                throw new DataNotFoundErrorExceptionHandler("Color not found: " + orderProducts.getColor());
            }
        }
        try {
            String sql = """
            INSERT INTO order_items (
                order_id,
                product_id,
                quantity,
                price,
                created_by,
                color_id
            ) VALUES (?, ?, ?, ?, ?, ?)
        """;

            int rows = jdbcTemplate.update(
                    sql,
                    orderId,
                    orderProducts.getProductId(),
                    orderProducts.getQuantity(),
                    orderProducts.getPrice(),
                    userId,
                    colorId
            );

            return rows > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<OrderResponse.printings> getPrintingsDetailsByOrderIdList(List<Long> printingOrderIds) {
        if (printingOrderIds.isEmpty()) return Collections.emptyList();

        // Build dynamic IN clause
        String inSql = printingOrderIds.stream().map(id -> "?").collect(Collectors.joining(","));
        String query = OrderQueries.GET_PRINTINGS_DETAILS_BY_ORDER_ID_LIST_PREFIX + inSql + ")";

        try {
            return jdbcTemplate.query(query,
                    printingOrderIds.toArray(),
                    (rs, rowNum) -> {
                        // Images
                        OrderResponse.Images image = null;
                        if (rs.getString("image_id") != null) {
                            image = OrderResponse.Images.builder()
                                    .imageId(rs.getString("image_id"))
                                    .imageUrl(rs.getString("image_url"))
                                    .isPrimary(rs.getBoolean("image_is_primary"))
                                    .imageStatus(rs.getString("image_status"))
                                    .createdAt(rs.getTimestamp("image_created_at"))
                                    .build();
                        }

                        // Files
                        OrderResponse.OrderFiles file = null;
                        if (rs.getString("file_id") != null) {
                            file = OrderResponse.OrderFiles.builder()
                                    .fileId(rs.getLong("file_id"))
                                    .fileName(rs.getString("file_name"))
                                    .fileUrl(rs.getString("file_url"))
                                    .build();
                        }

                        // Printer
                        OrderResponse.Printer printer = null;
                        if (rs.getString("printer_id") != null) {
                            printer = OrderResponse.Printer.builder()
                                    .printerId(rs.getLong("printer_id"))
                                    .printerName(rs.getString("printer_name"))
                                    .printerModel(rs.getString("printer_model"))
                                    .build();
                        }

                        return OrderResponse.printings.builder()
                                .orderId(rs.getLong("order_id"))
                                .printingOrderId(rs.getLong("printing_order_id"))
                                .productId(rs.getLong("product_id"))
                                .customText(rs.getString("custom_text"))
                                .description(rs.getString("description"))
                                .size(rs.getString("size"))
                                .color(rs.getString("color"))
                                .material(rs.getString("material"))
                                .quantity(rs.getInt("quantity"))
                                .unitPrice(rs.getDouble("unit_price"))
                                .status(rs.getString("status"))
                                .printer(printer)
                                .imagesList(image != null ? List.of(image) : new ArrayList<>())
                                .orderFilesList(file != null ? List.of(file) : new ArrayList<>())
                                .build();
                    });

        } catch (DataAccessException ex) {
            throw new DataAccessErrorExceptionHandler("Database error while fetching printing order details");
        }
    }
}