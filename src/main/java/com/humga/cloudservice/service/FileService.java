package com.humga.cloudservice.service;

import com.humga.cloudservice.model.entity.File;
import com.humga.cloudservice.repository.FileCrudRepository;
import com.humga.cloudservice.repository.UserCrudRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class FileService {
    private final FileCrudRepository fileRepo;
    private final UserCrudRepository userRepo;

    public FileService(FileCrudRepository fileRepo, UserCrudRepository userRepo) {
        this.fileRepo = fileRepo;
        this.userRepo = userRepo;
    }

    public void saveFile(String filename, byte[] bytes, String currentUserLogin) {
        fileRepo.save(
                (new File(filename, bytes)).setUser(
                        userRepo.getUserByLogin(currentUserLogin)
                                .orElseThrow(()-> new NoSuchElementException("Unable to save. User not found."))));
    }

    public void deleteFile(String filename, String currentUserLogin) {
        fileRepo.delete(
                fileRepo.findFileByFilenameAndUser_Login(filename, currentUserLogin)
                        .orElseThrow(()-> new NoSuchElementException("File not found.")));
    }

    public byte[] getFile(String filename, String currentUserLogin) {
        return fileRepo
                .findFileByFilenameAndUser_Login(filename, currentUserLogin)
                .orElseThrow(()-> new NoSuchElementException("File not found."))
                .getFile();
    }

    public void renameFile(String filename, String newname, String currentUserLogin) {
        fileRepo.save(
                fileRepo
                        .findFileByFilenameAndUser_Login(filename, currentUserLogin)
                        .orElseThrow(()-> new NoSuchElementException("File not found."))
                        .setFilename(newname));
    }

    public List<File> getFilesList(int limit, String currentUserLogin) {
        return fileRepo
                .findAllByUser_Login(currentUserLogin, PageRequest.of(0, limit, Sort.by("filename")))
                .getContent();
    }
}
