package com.kind.backend.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdatePostRequest {

    @Size(max = 200)
    private String title;

    @Size(max = 5000)
    private String body;

    private String type;

    private Long departmentId;

    /** true = пост для всех */
    private Boolean clearDepartment;
}
