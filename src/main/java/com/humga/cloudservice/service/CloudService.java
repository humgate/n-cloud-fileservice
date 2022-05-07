package com.humga.cloudservice.service;

import com.humga.cloudservice.repository.CloudRepository;
import org.springframework.stereotype.Service;

@Service
public class CloudService {
    private final CloudRepository repository;

    public CloudService(CloudRepository repository) {
        this.repository = repository;
    }

    public void saveFile(String filename, byte[] bytes) {
        repository.save("filename.txt", bytes);
    }
}
