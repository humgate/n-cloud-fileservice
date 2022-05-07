package com.humga.cloudservice.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity @Table(name = "files")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable = false)
    private User user;

    @Column(name = "file", nullable = false, columnDefinition = "blob")
    private byte[] file;
}
