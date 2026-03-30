package com.spoolbear.service.impl;

import com.spoolbear.exception.DataAccessErrorExceptionHandler;
import com.spoolbear.exception.DataNotFoundErrorExceptionHandler;
import com.spoolbear.exception.InternalServerErrorExceptionHandler;
import com.spoolbear.model.request.FetchCartRequest;
import com.spoolbear.model.request.InsertItemToCartRequest;
import com.spoolbear.model.request.RemoveAllFromCartRequest;
import com.spoolbear.model.request.RemoveItemFromCartRequest;
import com.spoolbear.model.response.*;
import com.spoolbear.repository.CartRepository;
import com.spoolbear.service.CartService;
import com.spoolbear.service.CommonService;
import com.spoolbear.util.CommonResponseMessages;
import com.spoolbear.validations.CartValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CartServiceImpl.class);

    private final CartRepository cartRepository;
    private final CartValidationService cartValidationService;
    private final CommonService commonService;

    @Autowired
    public CartServiceImpl(CartRepository cartRepository, CartValidationService cartValidationService, CommonService commonService) {
        this.cartRepository = cartRepository;
        this.cartValidationService = cartValidationService;
        this.commonService = commonService;
    }

    @Override
    public CommonResponse<CartCreateResponse> createCart(InsertItemToCartRequest insertItemToCartRequest) {
        LOGGER.info("Start create cart in repository");
        try {
            Long userId = null;
            userId = commonService.getUserIdBySecurityContextWithOutException();
            Long cartId = cartRepository.createCart(userId);
            insertItemToCartRequest.setCartId(cartId);
            CommonResponse<List<ProductsCartResponse>> products = addProductToCart(insertItemToCartRequest);
            CartCreateResponse cartCreateResponse = new CartCreateResponse();
            cartCreateResponse.setCardId(cartId);
            cartCreateResponse.setProductsCartResponse(products.getData().getFirst());
            return new CommonResponse<>(
                    CommonResponseMessages.SUCCESSFULLY_INSERT_CODE,
                    CommonResponseMessages.SUCCESSFULLY_INSERT_STATUS,
                    CommonResponseMessages.SUCCESSFULLY_INSERT_MESSAGE,
                    cartCreateResponse,
                    Instant.now());

        } catch (DataNotFoundErrorExceptionHandler | DataAccessErrorExceptionHandler e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error occurred while creating cart : {}", e.getMessage(), e);
            throw new InternalServerErrorExceptionHandler("Failed to creating cart from database");
        } finally {
            LOGGER.info("End fetching creating cart from repository");
        }
    }

    @Override
    public CommonResponse<List<ProductsCartResponse>> fetchCart(FetchCartRequest fetchCartRequest) {
        LOGGER.info("Start fetching products to cart from repository");
        try {
            cartValidationService.validateFetchCartRequest(fetchCartRequest);
            List<ProductsCartResponse> productsCartResponses = cartRepository.fetchCart(fetchCartRequest.getCartId());

            if (productsCartResponses.isEmpty()) {
                LOGGER.warn("No products found in database");
                throw new DataNotFoundErrorExceptionHandler("No products found");
            }

            LOGGER.info("Fetched {} products successfully", productsCartResponses.size());
            return new CommonResponse<>(
                    CommonResponseMessages.SUCCESSFULLY_RETRIEVE_CODE,
                    CommonResponseMessages.SUCCESSFULLY_RETRIEVE_STATUS,
                    CommonResponseMessages.SUCCESSFULLY_RETRIEVE_MESSAGE,
                    productsCartResponses,
                    Instant.now());

        } catch (DataNotFoundErrorExceptionHandler e) {
            return new CommonResponse<>(
                    CommonResponseMessages.DATA_NOT_FOUND_CODE,
                    CommonResponseMessages.DATA_NOT_FOUND_STATUS,
                    CommonResponseMessages.DATA_NOT_FOUND_MESSAGE,
                    new ArrayList<>(),
                    Instant.now());
        } catch (DataAccessErrorExceptionHandler e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error occurred while fetching products: {}", e.getMessage(), e);
            throw new InternalServerErrorExceptionHandler("Failed to fetch products from database");
        } finally {
            LOGGER.info("End fetching products from repository");
        }
    }

    @Override
    public CommonResponse<List<ProductsCartResponse>> addProductToCart(InsertItemToCartRequest insertItemToCartRequest) {
        LOGGER.info("Start add product to cart from repository");
        try {
            cartValidationService.validateInsertItemToCartRequest(insertItemToCartRequest);

            List<ProductsCartResponse> currentProducts = fetchCart(
                    new FetchCartRequest(insertItemToCartRequest.getCartId())
            ).getData();

            boolean isSuccessfullyAdded = false;

            for (ProductsCartResponse product : currentProducts) {
                if (product.getProductId().equals(insertItemToCartRequest.getProductId()) &&
                        product.getColor().equals(insertItemToCartRequest.getColor())) {
                    isSuccessfullyAdded = cartRepository.increaseCartProductQuantityByOne(product.getCartItemId());
                    break;
                }
            }

            if (!isSuccessfullyAdded) {
                isSuccessfullyAdded = cartRepository.addProductToCart(insertItemToCartRequest);
            }


            if (isSuccessfullyAdded) {
                cartRepository.decreaseProductQuantityByOne(insertItemToCartRequest.getProductId());
                List<ProductsCartResponse> products = fetchCart(new FetchCartRequest(insertItemToCartRequest.getCartId())).getData();
                return new CommonResponse<>(
                        CommonResponseMessages.SUCCESSFULLY_RETRIEVE_CODE,
                        CommonResponseMessages.SUCCESSFULLY_RETRIEVE_STATUS,
                        CommonResponseMessages.SUCCESSFULLY_RETRIEVE_MESSAGE,
                        products,
                        Instant.now());
            } else {
                return new CommonResponse<>(
                        CommonResponseMessages.UNSUCCESSFULLY_INSERT_CODE,
                        CommonResponseMessages.UNSUCCESSFULLY_INSERT_STATUS,
                        CommonResponseMessages.UNSUCCESSFULLY_INSERT_MESSAGE,
                        new ArrayList<>(),
                        Instant.now());
            }

        } catch (DataNotFoundErrorExceptionHandler | DataAccessErrorExceptionHandler e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error occurred while add product : {}", e.getMessage(), e);
            throw new InternalServerErrorExceptionHandler("Failed to add product to database");
        } finally {
            LOGGER.info("End add product to repository");
        }
    }

    @Override
    public CommonResponse<List<ProductsCartResponse>> removeProductFromCart(RemoveItemFromCartRequest removeItemFromCartRequest) {
        LOGGER.info("Start remove product in cart from repository");
        try {
            cartValidationService.validateRemoveItemFromCartRequest(removeItemFromCartRequest);
            int currentQuantity = cartRepository.getCurrentQuantity(removeItemFromCartRequest.getCartItemId());
            if (currentQuantity > 1) {
                cartRepository.decreaseCartProductQuantityByOne(removeItemFromCartRequest.getCartItemId());
                cartRepository.increaseProductQuantityByOne(removeItemFromCartRequest.getProductId());
            } else if (currentQuantity == 1) {
                cartRepository.removeProductFromCart(removeItemFromCartRequest.getCartItemId());
                cartRepository.increaseProductQuantityByOne(removeItemFromCartRequest.getProductId());
            }

            List<ProductsCartResponse> products = fetchCart(new FetchCartRequest(removeItemFromCartRequest.getCartId())).getData();

            return new CommonResponse<>(
                    CommonResponseMessages.SUCCESSFULLY_REMOVE_CODE,
                    CommonResponseMessages.SUCCESSFULLY_REMOVE_STATUS,
                    CommonResponseMessages.SUCCESSFULLY_REMOVE_MESSAGE,
                    products,
                    Instant.now());

        } catch (DataNotFoundErrorExceptionHandler | DataAccessErrorExceptionHandler e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error occurred while remove product: {}", e.getMessage(), e);
            throw new InternalServerErrorExceptionHandler("Failed to remove product from database");
        } finally {
            LOGGER.info("End remove product from repository");
        }
    }

    @Override
    public CommonResponse<RemoveAllProductsResponse> removeAllFromCart(RemoveAllFromCartRequest removeAllFromCartRequest) {
        LOGGER.info("Start remove all products in cart from repository");
        try {
            cartValidationService.validateRemoveAllFromCartRequest(removeAllFromCartRequest);
            List<ProductsCartResponse> productsCartResponses = cartRepository.fetchCart(removeAllFromCartRequest.getCartId());

            for (ProductsCartResponse productsCartResponse : productsCartResponses) {
                Integer productQuantity = productsCartResponse.getQuantity();
                if (productQuantity == 1) {
                    cartRepository.removeProductFromCart(productsCartResponse.getCartItemId());
                    cartRepository.increaseProductQuantityByOne(productsCartResponse.getProductId());
                } else if (productQuantity > 1) {
                    for (int i = 0; i < productQuantity; i++) {
                        cartRepository.decreaseCartProductQuantityByOne(productsCartResponse.getCartItemId());
                        cartRepository.increaseProductQuantityByOne(productsCartResponse.getProductId());
                    }
                    cartRepository.removeProductFromCart(productsCartResponse.getCartItemId());
                }
            }

            return new CommonResponse<>(
                    CommonResponseMessages.SUCCESSFULLY_REMOVE_CODE,
                    CommonResponseMessages.SUCCESSFULLY_REMOVE_STATUS,
                    CommonResponseMessages.SUCCESSFULLY_REMOVE_MESSAGE,
                    new RemoveAllProductsResponse("Successfully remove all"),
                    Instant.now());

        } catch (DataNotFoundErrorExceptionHandler | DataAccessErrorExceptionHandler e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error occurred while remove all products: {}", e.getMessage(), e);
            throw new InternalServerErrorExceptionHandler("Failed to remove all products from database");
        } finally {
            LOGGER.info("End remove all products from repository");
        }
    }

    @Override
    public CommonResponse<CartIdResponse> fetchCartId() {
        LOGGER.info("Start fetch cart id from repository");
        try {
            Long userId = null;
            userId = commonService.getUserIdBySecurityContextWithOutException();
            if (userId != null) {
                Long cartId = null;
                cartId = cartRepository.fetchCartId(userId);
                if (cartId != null) {
                    return new CommonResponse<>(
                            CommonResponseMessages.SUCCESSFULLY_RETRIEVE_CODE,
                            CommonResponseMessages.SUCCESSFULLY_RETRIEVE_STATUS,
                            CommonResponseMessages.SUCCESSFULLY_RETRIEVE_MESSAGE,
                            new CartIdResponse(cartId),
                            Instant.now());
                }
            }
            return new CommonResponse<>(
                    CommonResponseMessages.SUCCESSFULLY_RETRIEVE_CODE,
                    CommonResponseMessages.SUCCESSFULLY_RETRIEVE_STATUS,
                    CommonResponseMessages.SUCCESSFULLY_RETRIEVE_MESSAGE,
                    new CartIdResponse(null),
                    Instant.now());


        } catch (DataNotFoundErrorExceptionHandler | DataAccessErrorExceptionHandler e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error occurred while remove product: {}", e.getMessage(), e);
            throw new InternalServerErrorExceptionHandler("Failed to remove product from database");
        } finally {
            LOGGER.info("End remove product from repository");
        }
    }

    @Override
    public CommonResponse<List<ProductsCartResponse>> removeProductAllItemsFromCart(RemoveItemFromCartRequest removeItemFromCartRequest) {
        LOGGER.info("Start remove product all items in cart from repository");
        try {
            cartValidationService.validateRemoveItemFromCartRequest(removeItemFromCartRequest);
            cartRepository.removeProductFromCart(removeItemFromCartRequest.getCartItemId());
            List<ProductsCartResponse> products = fetchCart(new FetchCartRequest(removeItemFromCartRequest.getCartId())).getData();

            return new CommonResponse<>(
                    CommonResponseMessages.SUCCESSFULLY_REMOVE_CODE,
                    CommonResponseMessages.SUCCESSFULLY_REMOVE_STATUS,
                    CommonResponseMessages.SUCCESSFULLY_REMOVE_MESSAGE,
                    products,
                    Instant.now());

        } catch (DataNotFoundErrorExceptionHandler | DataAccessErrorExceptionHandler e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error occurred while remove product: {}", e.getMessage(), e);
            throw new InternalServerErrorExceptionHandler("Failed to remove product from database");
        } finally {
            LOGGER.info("End remove product all items from repository");
        }
    }

    @Override
    public void boughtProductAllItemsFromCart(Long cartItemId) {
        LOGGER.info("Start bought product all items in cart from repository");
        try {
            cartRepository.boughtProductAllItemsFromCart(cartItemId);
        } catch (DataNotFoundErrorExceptionHandler | DataAccessErrorExceptionHandler e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error occurred while bought product: {}", e.getMessage(), e);
            throw new InternalServerErrorExceptionHandler("Failed to bought product from database");
        } finally {
            LOGGER.info("End bought product all items from repository");
        }
    }
}

