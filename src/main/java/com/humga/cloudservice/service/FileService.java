package com.humga.cloudservice.service;

import com.humga.cloudservice.entity.File;
import com.humga.cloudservice.repository.CloudRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FileService {
    private final CloudRepository repository;

    public FileService(CloudRepository repository) {
        this.repository = repository;
    }

    public void saveFile(String filename, byte[] bytes) {
        repository.saveFile(filename, bytes);
    }

    public void deleteFile(String filename) {
        repository.deleteFile(filename);
    }

    public byte[] getFile(String filename) {
        return repository.getFile(filename).getFile();
    }

    public void renameFile(String filename, String newname) {
        repository.renameFile(filename, newname);
    }

    public List<File> getFilesList(int limit) {
        return repository.getFilesList(limit);
    }

}
