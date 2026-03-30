package com.spoolbear.repository.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spoolbear.exception.DataAccessErrorExceptionHandler;
import com.spoolbear.exception.DataNotFoundErrorExceptionHandler;
import com.spoolbear.exception.InternalServerErrorExceptionHandler;
import com.spoolbear.model.request.InsertItemToCartRequest;
import com.spoolbear.model.request.RemoveItemFromCartRequest;
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
    public boolean addProductToCart(InsertItemToCartRequest request) {
        try {
            LOGGER.info("Adding product to cart: {}", request);

            // Step 1: Resolve color_id (can be null)
            Long colorId = null;
            if (request.getColor() != null) {
                try {
                    colorId = jdbcTemplate.queryForObject(
                            CartQueries.GET_COLOR_ID_BY_NAME,
                            new Object[]{request.getColor()},
                            Long.class
                    );
                } catch (EmptyResultDataAccessException ex) {
                    LOGGER.warn("Color not found: {}", request.getColor());
                    throw new DataNotFoundErrorExceptionHandler("Color not found: " + request.getColor());
                }
            }
            Long resolvedColorId = colorId;
            int rowsAffected = jdbcTemplate.update(
                    CartQueries.INSERT_CART_ITEM,
                    ps -> {
                        ps.setLong(1, request.getCartId());
                        ps.setLong(2, request.getProductId());

                        if (resolvedColorId != null) {
                            ps.setLong(3, resolvedColorId);
                        } else {
                            ps.setNull(3, Types.BIGINT);
                        }

                        ps.setInt(4, request.getQuantity());

                        if (request.getCartId() != null) {
                            ps.setLong(5, request.getCartId());
                        } else {
                            ps.setNull(5, Types.BIGINT);
                        }
                    }
            );

            LOGGER.info("Product added to cart successfully. Rows affected: {}", rowsAffected);
            return rowsAffected > 0;

        } catch (DataNotFoundErrorExceptionHandler ex) {
            throw ex;
        } catch (DataAccessException ex) {
            LOGGER.error("Database error while adding product to cart: {}", ex.getMessage(), ex);
            throw new DataAccessErrorExceptionHandler("Failed to add product to cart");
        } catch (Exception ex) {
            LOGGER.error("Unexpected error: {}", ex.getMessage(), ex);
            throw new InternalServerErrorExceptionHandler("Unexpected error occurred while adding product to cart");
        }
    }

    @Override
    public void decreaseProductQuantityByOne(Long productId) {
        try {
            LOGGER.info("Decreasing stock for productId: {}", productId);

            int rowsAffected = jdbcTemplate.update(CartQueries.DECREASE_PRODUCT_STOCK, productId);

            if (rowsAffected == 0) {
                LOGGER.warn("Product not found or stock already zero for productId: {}", productId);
                throw new DataNotFoundErrorExceptionHandler("Product not found or stock is zero: " + productId);
            }

        } catch (DataAccessException ex) {
            LOGGER.error("Database error while decreasing product stock: {}", ex.getMessage(), ex);
            throw new DataAccessErrorExceptionHandler("Failed to decrease product stock");
        } catch (Exception ex) {
            LOGGER.error("Unexpected error while decreasing product stock: {}", ex.getMessage(), ex);
            throw new InternalServerErrorExceptionHandler("Unexpected error occurred while decreasing product stock");
        }
    }

    @Override
    public int getCurrentQuantity(Long cartItemId) {
        try {
            LOGGER.info("Fetching current quantity for cartItemId: {}", cartItemId);

            Integer quantity = jdbcTemplate.queryForObject(
                    CartQueries.GET_CART_ITEM_QUANTITY,
                    new Object[]{cartItemId},
                    Integer.class
            );

            if (quantity == null) {
                LOGGER.warn("Cart item not found or inactive: {}", cartItemId);
                throw new DataNotFoundErrorExceptionHandler("Cart item not found or inactive: " + cartItemId);
            }

            return quantity;

        } catch (EmptyResultDataAccessException ex) {
            LOGGER.warn("Cart item not found: {}", cartItemId);
            throw new DataNotFoundErrorExceptionHandler("Cart item not found: " + cartItemId);
        } catch (DataAccessException ex) {
            LOGGER.error("Database error while fetching cart item quantity: {}", ex.getMessage(), ex);
            throw new DataAccessErrorExceptionHandler("Failed to fetch cart item quantity");
        } catch (Exception ex) {
            LOGGER.error("Unexpected error while fetching cart item quantity: {}", ex.getMessage(), ex);
            throw new InternalServerErrorExceptionHandler("Unexpected error occurred while fetching cart item quantity");
        }
    }

    @Override
    public void decreaseCartProductQuantityByOne(Long cartItemId) {
        try {
            LOGGER.info("Decreasing quantity for cartItemId: {}", cartItemId);

            int rows = jdbcTemplate.update(
                    CartQueries.DECREASE_QUANTITY,
                    cartItemId
            );

            if (rows == 0) {
                LOGGER.warn("No cart item found or quantity already zero: {}", cartItemId);
                throw new DataNotFoundErrorExceptionHandler("Cart item not found or quantity already zero");
            }

        } catch (DataAccessException ex) {
            LOGGER.error("Database error while decreasing quantity: {}", ex.getMessage(), ex);
            throw new DataAccessErrorExceptionHandler("Failed to decrease cart item quantity");
        } catch (Exception ex) {
            LOGGER.error("Unexpected error: {}", ex.getMessage(), ex);
            throw new InternalServerErrorExceptionHandler("Unexpected error occurred while decreasing quantity");
        }
    }

    @Override
    public void increaseProductQuantityByOne(Long productId) {
        try {
            LOGGER.info("Increasing stock for productId: {}", productId);

            int rowsAffected = jdbcTemplate.update(CartQueries.INCREASE_PRODUCT_STOCK, productId);

            if (rowsAffected == 0) {
                LOGGER.warn("Product not found to increase stock: {}", productId);
                throw new DataNotFoundErrorExceptionHandler("Product not found: " + productId);
            }

        } catch (DataAccessException ex) {
            LOGGER.error("Database error while increasing product stock: {}", ex.getMessage(), ex);
            throw new DataAccessErrorExceptionHandler("Failed to increase product stock");
        } catch (Exception ex) {
            LOGGER.error("Unexpected error while increasing product stock: {}", ex.getMessage(), ex);
            throw new InternalServerErrorExceptionHandler("Unexpected error occurred while increasing product stock");
        }
    }

    @Override
    public void removeProductFromCart(Long cartItemId) {
        try {
            LOGGER.info("Removing cart item (soft delete) for cartItemId: {}", cartItemId);

            // Step 1: Get TERMINATED status ID
            Integer terminatedStatusId = jdbcTemplate.queryForObject(
                    CartQueries.GET_TERMINATED_STATUS_ID,
                    Integer.class
            );

            if (terminatedStatusId == null) {
                throw new DataNotFoundErrorExceptionHandler("TERMINATED status not found");
            }

            // Step 2: Update status
            int rows = jdbcTemplate.update(
                    CartQueries.REMOVE_CART_ITEM,
                    terminatedStatusId,
                    cartItemId
            );

            if (rows == 0) {
                LOGGER.warn("Cart item not found: {}", cartItemId);
                throw new DataNotFoundErrorExceptionHandler("Cart item not found");
            }

        } catch (DataAccessException ex) {
            LOGGER.error("Database error while removing cart item: {}", ex.getMessage(), ex);
            throw new DataAccessErrorExceptionHandler("Failed to remove cart item");
        } catch (Exception ex) {
            LOGGER.error("Unexpected error: {}", ex.getMessage(), ex);
            throw new InternalServerErrorExceptionHandler("Unexpected error occurred while removing cart item");
        }
    }

    @Override
    public boolean increaseCartProductQuantityByOne(Long cartItemId) {
        try {
            LOGGER.info("Increasing quantity for cartItemId: {}", cartItemId);

            int rowsAffected = jdbcTemplate.update(
                    CartQueries.INCREASE_QUANTITY,
                    cartItemId
            );

            if (rowsAffected == 0) {
                LOGGER.warn("No active cart item found to increase quantity: {}", cartItemId);
                throw new DataNotFoundErrorExceptionHandler("Cart item not found or not active");
            }

            LOGGER.info("Quantity increased successfully for cartItemId: {}", cartItemId);
            return true;

        } catch (DataAccessException ex) {
            LOGGER.error("Database error while increasing cart item quantity: {}", ex.getMessage(), ex);
            throw new DataAccessErrorExceptionHandler("Failed to increase cart item quantity");
        } catch (Exception ex) {
            LOGGER.error("Unexpected error while increasing cart item quantity: {}", ex.getMessage(), ex);
            throw new InternalServerErrorExceptionHandler("Unexpected error occurred while increasing cart item quantity");
        }
    }

    @Override
    public Long fetchCartId(Long userId) {
        String sql = "SELECT c.cart_id FROM cart c WHERE c.user_id = ? LIMIT 1";

        try {
            return jdbcTemplate.queryForObject(
                    sql,
                    new Object[]{userId},
                    Long.class
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public void boughtProductAllItemsFromCart(Long cartItemId) {
        try {
            LOGGER.info("bought cart item (soft delete) for cartItemId: {}", cartItemId);

            Integer boughtStatusId = jdbcTemplate.queryForObject(
                    CartQueries.GET_BOUGHT_STATUS_ID,
                    Integer.class
            );

            if (boughtStatusId == null) {
                throw new DataNotFoundErrorExceptionHandler("BOUGHT status not found");
            }

            // Step 2: Update status
            int rows = jdbcTemplate.update(
                    CartQueries.BOUGHT_CART_ITEM,
                    boughtStatusId,
                    cartItemId
            );

            if (rows == 0) {
                LOGGER.warn("Cart item not found: {}", cartItemId);
                throw new DataNotFoundErrorExceptionHandler("Cart item not found");
            }

        } catch (DataAccessException ex) {
            LOGGER.error("Database error while removing cart item: {}", ex.getMessage(), ex);
            throw new DataAccessErrorExceptionHandler("Failed to remove cart item");
        } catch (Exception ex) {
            LOGGER.error("Unexpected error: {}", ex.getMessage(), ex);
            throw new InternalServerErrorExceptionHandler("Unexpected error occurred while removing cart item");
        }
    }

}
