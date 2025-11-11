package com.example.CMCmp3.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class SongLikeId implements Serializable {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "song_id")
    private Long songId;
}
