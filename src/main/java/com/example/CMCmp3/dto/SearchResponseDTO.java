package com.example.CMCmp3.dto;

import lombok.Data;
import java.util.List;

@Data
public class SearchResponseDTO {
    private List<SongDTO> songs;
    private List<ArtistDTO> artists;
    private List<PlaylistDTO> playlists;
}
