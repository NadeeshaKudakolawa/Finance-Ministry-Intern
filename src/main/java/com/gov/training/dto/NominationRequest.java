package com.gov.training.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NominationRequest {

    private Long trainingId;
    private Long officerId;
    private Long departmentId;
}
