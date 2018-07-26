package com.dekalabs.magentorestapi.dto;

import java.util.List;

public class MagentoResponse<DATA> {

    private DATA data;

    private MagentoError error;


    public DATA getData() {
        return data;
    }

    public void setData(DATA data) {
        this.data = data;
    }

    public MagentoError getError() {
        return error;
    }

    public void setError(MagentoError error) {
        this.error = error;
    }
}
