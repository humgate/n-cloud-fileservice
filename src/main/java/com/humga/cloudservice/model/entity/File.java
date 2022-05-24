package com.humga.cloudservice.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity @Table(name = "files")
@Getter @Setter @NoArgsConstructor
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable = false)
    private User user;

    @Column(name="filename", nullable = false)
    private String filename;
    @Column(name = "data", nullable = false, columnDefinition = "blob")
    private byte[] file;

    public File(String filename, byte[] file) {
        this.filename=filename;
        this.file = file;
    }

    /**
     * Additional custom setter returning File
     *
     * @param filename - file name
     * @return this
     */
    public File setFilename(String filename) {
        this.filename=filename;
        return this;
    }

    public File setUser(User user) {
        this.user = user;
        return this;
    }
}
