package com.spoolbear.service;

import com.spoolbear.model.request.MaterialDetailsRequest;
import com.spoolbear.model.response.CommonResponse;
import com.spoolbear.model.response.MaterialDetailsResponse;

import java.util.List;

public interface MaterialService {
    CommonResponse<List<MaterialDetailsResponse>> getAllMaterials();

    CommonResponse<MaterialDetailsResponse> getMaterialsDetailsById(MaterialDetailsRequest materialDetailsRequest);
}
