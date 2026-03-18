package com.spoolbear.service;

import com.spoolbear.model.response.CommonResponse;
import com.spoolbear.model.response.GalleryResponse;

import java.util.List;

public interface GalleryService {
    CommonResponse<List<GalleryResponse>> getActiveImages();
}
