package com.example.CMCmp3.repository;

import com.example.CMCmp3.entity.SongLike;
import com.example.CMCmp3.entity.SongLikeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SongLikeRepository extends JpaRepository<SongLike, SongLikeId> {
}
