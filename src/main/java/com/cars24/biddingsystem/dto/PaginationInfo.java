package com.cars24.biddingsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PaginationInfo {
    private int offset;
    private int limit;
    private int nextPage;
    private int previousPage;

    public PaginationInfo(int offset, int limit) {
        this.limit = limit;
        this.offset = offset;
    }
}
