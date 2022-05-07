package com.humga.cloudservice.repository;

import com.humga.cloudservice.entity.File;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FilePagingAndSortingRepository extends PagingAndSortingRepository<File, Long> {
}
