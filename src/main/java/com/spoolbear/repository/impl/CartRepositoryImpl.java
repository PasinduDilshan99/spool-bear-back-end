package com.spoolbear.repository.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spoolbear.exception.DataAccessErrorExceptionHandler;
import com.spoolbear.exception.DataNotFoundErrorExceptionHandler;
import com.spoolbear.exception.InternalServerErrorExceptionHandler;
import com.spoolbear.model.request.InsertItemToCartRequest;
import com.spoolbear.model.response.BlogResponse;
import com.spoolbear.model.response.ProductsCartResponse;
import com.spoolbear.queries.BlogQueries;
import com.spoolbear.queries.CartQueries;
import com.spoolbear.repository.CartRepository;
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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
public class CartRepositoryImpl implements CartRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(CartRepositoryImpl.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public CartRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Long createCart(Long userId) {
        try {
            LOGGER.info("Creating a new cart for userId: {}", userId);

            String INSERT_CART_SQL = """
                INSERT INTO cart (user_id, created_by)
                VALUES (?, ?)
                """;

            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(INSERT_CART_SQL, Statement.RETURN_GENERATED_KEYS);
                if (userId != null) {
                    ps.setLong(1, userId);
                    ps.setLong(2, userId);
                } else {
                    ps.setNull(1, Types.BIGINT);
                    ps.setNull(2, Types.BIGINT);
                }
                return ps;
            }, keyHolder);

            Long cartId = keyHolder.getKey().longValue();
            LOGGER.info("Created new cart with cartId: {}", cartId);
            return cartId;

        } catch (DataAccessException ex) {
            LOGGER.error("Database error while creating cart for userId {}: {}", userId, ex.getMessage(), ex);
            throw new DataAccessErrorExceptionHandler("Failed to create cart");
        } catch (Exception ex) {
            LOGGER.error("Unexpected error while creating cart for userId {}: {}", userId, ex.getMessage(), ex);
            throw new InternalServerErrorExceptionHandler("Unexpected error occurred while creating cart");
        }
    }

    @Override
    public List<ProductsCartResponse> fetchCart(Long cartId) {
        String GET_PRODUCTS_BY_CART_ID = CartQueries.GET_PRODUCTS_BY_CART_ID;

        try {
            LOGGER.info("Executing query to fetch cart products for ID: {}", cartId);

            Map<Long, ProductsCartResponse> cartItemMap = new LinkedHashMap<>();

            jdbcTemplate.query(GET_PRODUCTS_BY_CART_ID, new Object[]{cartId}, rs -> {
                Long cartItemId = rs.getLong("cart_item_id");

                ProductsCartResponse cartItem = cartItemMap.get(cartItemId);
                if (cartItem == null) {
                    cartItem = ProductsCartResponse.builder()
                            .cartId(rs.getLong("cart_id"))
                            .cartItemId(cartItemId)
                            .productId(rs.getLong("product_id"))
                            .name(rs.getString("product_name"))
                            .price(rs.getDouble("price"))
                            .quantity(rs.getInt("quantity"))
                            .materialId(rs.getLong("material_id"))
                            .material(rs.getString("material_name"))
                            .typeId(rs.getLong("type_id"))
                            .type(rs.getString("type_name"))
                            .color(rs.getString("color_name"))
                            .colorCode(rs.getString("color_code"))
                            .images(new ArrayList<>())
                            .build();

                    cartItemMap.put(cartItemId, cartItem);
                }

                Long imageId = rs.getLong("image_id");
                String imageUrl = rs.getString("image_url");
                if (imageId != 0 && imageUrl != null) {
                    ProductsCartResponse.Image image = ProductsCartResponse.Image.builder()
                            .id(imageId)
                            .name(rs.getString("product_name"))
                            .url(imageUrl)
                            .description(null)
                            .build();
                    cartItem.getImages().add(image);
                }
            });

            return new ArrayList<>(cartItemMap.values());

        } catch (EmptyResultDataAccessException ex) {
            LOGGER.error("Products not found with cart ID: {}", cartId);
            throw new DataNotFoundErrorExceptionHandler("Products not found for cart ID: " + cartId);
        } catch (DataAccessException ex) {
            LOGGER.error("Database error while fetching Products details: {}", ex.getMessage(), ex);
            throw new DataAccessErrorExceptionHandler("Failed to fetch Products details from database");
        } catch (Exception ex) {
            LOGGER.error("Unexpected error: {}", ex.getMessage(), ex);
            throw new InternalServerErrorExceptionHandler("Unexpected error occurred while fetching Products details");
        }
    }

    @Override
    public boolean addProductToCart(InsertItemToCartRequest insertItemToCartRequest) {
        return false;
    }

    @Override
    public void decreaseProductQuantityByOne(Long productId) {

    }

    @Override
    public int getCurrentQuantity(Long cartItemId) {
        return 0;
    }

    @Override
    public void decreaseCartProductQuantityByOne(Long cartItemId) {

    }

    @Override
    public void increaseProductQuantityByOne(Long productId) {

    }

    @Override
    public void removeProductFromCart(Long cartItemId) {

    }
}
