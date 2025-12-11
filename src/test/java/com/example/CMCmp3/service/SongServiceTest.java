package com.example.CMCmp3.service;

import com.example.CMCmp3.dto.SongDTO;
import com.example.CMCmp3.entity.Song;
import com.example.CMCmp3.repository.SongRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SongServiceTest {

    @Mock
    private SongRepository songRepository;

    @InjectMocks
    private SongService songService;

    @Test
    void searchByLyric() {
        // Given
        String query = "test lyric";
        Song song = new Song();
        song.setId(1L);
        song.setTitle("Test Song");
        List<Song> songs = Collections.singletonList(song);
        when(songRepository.searchByLyric(query)).thenReturn(songs);

        // When
        List<SongDTO> result = songService.searchByLyric(query);

        // Then
        assertEquals(1, result.size());
    }
}
