package com.ctang.zephyrcentrum.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import com.ctang.zephyrcentrum.types.Visibility;

@Entity
@Data
@NoArgsConstructor
@Table(name = "images", schema = "system")
public class Image {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name")
    private String name;
    
    @Column(name = "content_type")
    private String contentType;
    
    @Column(name = "size")
    private Long size;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "upload_date")
    private LocalDateTime uploadDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "visibility")
    private Visibility visibility;
    
    @Column(name = "data", columnDefinition = "BYTEA")
    private byte[] data;
    
    @Column(name = "user_id")
    private Long userId;
}