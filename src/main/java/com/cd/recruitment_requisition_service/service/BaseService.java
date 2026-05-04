package com.cd.recruitment_requisition_service.service;

import com.cd.recruitment_requisition_service.utils.BaseResponse;

public interface BaseService<T, ID>{

    BaseResponse save(T entity);
    BaseResponse update(ID id, T entity);
    BaseResponse deleteById(ID id);
    BaseResponse findById(ID id);
    BaseResponse findAllWithPagination(int pageNo, int pageSize);
    BaseResponse findAll();
}
