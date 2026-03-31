package com.spoolbear.repository;

import com.spoolbear.model.request.MaterialDetailsRequest;
import com.spoolbear.model.response.MaterialDetailsResponse;

import java.util.List;

public interface MaterialRepository {
    List<MaterialDetailsResponse> getAllMaterials();

    MaterialDetailsResponse getMaterialsDetailsById(MaterialDetailsRequest materialDetailsRequest);
}
