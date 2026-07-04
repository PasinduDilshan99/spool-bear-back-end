package com.spoolbear.service;

import com.spoolbear.model.request.WishListInsertRequest;
import com.spoolbear.model.response.CommonResponse;
import com.spoolbear.model.response.InsertResponse;
import com.spoolbear.model.response.WishListResponse;

import java.util.List;

public interface WishListService {
    CommonResponse<List<WishListResponse>> getWishListDetails();

    CommonResponse<InsertResponse> addAWishList(WishListInsertRequest wishListInsertRequest);
}
