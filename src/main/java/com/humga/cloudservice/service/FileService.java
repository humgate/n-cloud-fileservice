package com.humga.cloudservice.service;

import com.humga.cloudservice.model.entity.File;
import com.humga.cloudservice.repository.FileCrudRepository;
import com.humga.cloudservice.repository.UserCrudRepository;
import com.humga.cloudservice.util.Util;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FileService {
    private final FileCrudRepository fileRepo;
    private final UserCrudRepository userRepo;

    public FileService(FileCrudRepository fileRepo, UserCrudRepository userRepo) {
        this.fileRepo = fileRepo;
        this.userRepo = userRepo;
    }

    public void saveFile(String filename, byte[] bytes) {
        fileRepo.save(
                (new File(filename, bytes))
                        .setUser(userRepo.getUserByLogin(Util.getSessionUserLogin()).orElseThrow()));
    }

    public void deleteFile(String filename) {
        fileRepo.delete(
                fileRepo.findFileByFilenameAndUser_Login(filename, Util.getSessionUserLogin()).orElseThrow());
    }

    public byte[] getFile(String filename) {
        return fileRepo
                .findFileByFilenameAndUser_Login(filename, Util.getSessionUserLogin()).orElseThrow()
                .getFile();
    }

    public void renameFile(String filename, String newname) {
        fileRepo.save(
                fileRepo
                        .findFileByFilenameAndUser_Login(filename, Util.getSessionUserLogin()).orElseThrow()
                        .setFilename(newname));
    }

    public List<File> getFilesList(int limit) {
        return fileRepo
                .findAllByUser_Login(Util.getSessionUserLogin(), PageRequest.of(0, limit, Sort.by("filename")))
                .getContent();
    }
}
