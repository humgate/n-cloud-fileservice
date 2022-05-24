package com.humga.cloudservice.repository;

import com.humga.cloudservice.model.entity.File;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class FileRepository {
    private final UserCrudRepository userRepo;
    private final FileCrudRepository fileRepo;

    public FileRepository(UserCrudRepository userRepo, FileCrudRepository fileRepo) {
        this.userRepo = userRepo;
        this.fileRepo = fileRepo;
    }

    public void saveFile(String filename, byte[] bytes) {
        File file = new File(filename, bytes);
        file.setUser(userRepo.getUserById(1).orElseThrow());
        fileRepo.save(file);
    }

    public void deleteFile(String filename) {
        fileRepo.delete(fileRepo.findFileByFilename(filename).orElseThrow());
    }

    public File getFile(String filename) {
        return fileRepo.findFileByFilename(filename).orElseThrow();
    }

    public void renameFile(String filename, String newname) {
        fileRepo.save(
                fileRepo.findFileByFilename(filename).orElseThrow().setFilename(newname)
        );
    }

    public List<File> getFilesList(int countLimit) {
        return fileRepo.findAll(PageRequest.of(0, countLimit, Sort.by("filename"))).getContent();
    }

}
