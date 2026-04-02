package com.spoolbear.repository;

import com.spoolbear.model.dto.ExistWishListDataDto;
import com.spoolbear.model.request.WishListInsertRequest;
import com.spoolbear.model.response.WishListResponse;

import java.util.List;

public interface WishListRepository {
    ExistWishListDataDto getExistingWishListData(Long userId, WishListInsertRequest wishListInsertRequest);

    Long addWishList(WishListInsertRequest wishListInsertRequest, Long userId);

    void addWishListHistory(WishListInsertRequest wishListInsertRequest, Long userId, Long wishListId, String active);

    void updateWishList(WishListInsertRequest wishListInsertRequest, Long userId, ExistWishListDataDto existActivityWishListDataDto);

    List<WishListResponse> getWishListDetails(Long userId);
}
