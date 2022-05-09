package com.humga.cloudservice.repository;

import com.humga.cloudservice.entity.File;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FileCrudRepository extends CrudRepository<File, Long> {
    Optional<File> findFileByFilename(String filename);

    /* Slice<T> возвращает одну страницу и не делает count(*) запрос, чтобы получить общее кол-во страниц.
      Как па то что нам нужно для получения из базы на запрос /list c указанным лимитом на кол-во
      возвращаемых записей */
    Slice<File> findAll(Pageable pageable);
}