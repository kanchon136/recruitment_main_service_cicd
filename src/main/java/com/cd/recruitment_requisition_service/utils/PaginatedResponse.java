package com.cd.recruitment_requisition_service.utils;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaginatedResponse<T> {

    private List<T> payload;
    private int pageNo;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean last;


    public static <T> PaginatedResponse<T> fromPage(Page<T> page) {
        return new PaginatedResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );

    }

    public static <T> PaginatedResponse<T> empty() {
        return new PaginatedResponse<>(
                Collections.emptyList(), // payload
                0,                       // pageNo
                0,                       // pageSize
                0L,                      // totalElements
                0,                       // totalPages
                true                     // last
        );
    }

}
