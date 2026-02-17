package com.printing._d.service;

import com.printing._d.model.response.CommonResponse;
import com.printing._d.model.response.GalleryResponse;

import java.util.List;

public interface GalleryService {
    CommonResponse<List<GalleryResponse>> getActiveImages();
}
