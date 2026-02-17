package com.printing._d.repository;

import com.printing._d.model.response.GalleryResponse;

import java.util.List;

public interface GalleryRepository {
    List<GalleryResponse> getActiveImages();
}
