package com.humga.cloudservice.repository;

import com.humga.cloudservice.model.entity.File;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FileCrudRepository extends CrudRepository<File, Long> {
    Optional<File> findFileByFilenameAndUser_Login(String filename, String login);

    // Slice<T> возвращает одну страницу и не делает count(*) запрос, чтобы получить общее кол-во страниц.
    Slice<File> findAllByUser_Login(String login, Pageable pageable);
}