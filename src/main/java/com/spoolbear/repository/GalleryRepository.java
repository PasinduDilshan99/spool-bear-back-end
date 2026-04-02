package com.spoolbear.repository;

import com.spoolbear.model.response.GalleryResponse;

import java.util.List;

public interface GalleryRepository {
    List<GalleryResponse> getActiveImages();
}
