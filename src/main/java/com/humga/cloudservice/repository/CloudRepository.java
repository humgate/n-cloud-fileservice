package com.humga.cloudservice.repository;

import com.humga.cloudservice.entity.File;
import org.springframework.stereotype.Repository;

@Repository
public class CloudRepository {
    private final UserCrudRepository userRepo;
    private final FilePagingAndSortingRepository fileRepo;

    public CloudRepository(UserCrudRepository userRepo, FilePagingAndSortingRepository fileRepo) {
        this.userRepo = userRepo;
        this.fileRepo = fileRepo;
    }

    public void save(String filename, byte[] bytes) {
        File file = new File(filename, bytes);
        file.setUser(userRepo.getUserById(1).orElseThrow());
        fileRepo.save(file);
    }
}
