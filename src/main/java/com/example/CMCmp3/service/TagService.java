package com.example.CMCmp3.service;

import com.example.CMCmp3.dto.TagDTO;
import com.example.CMCmp3.dto.CreateTagDTO;
import com.example.CMCmp3.dto.UpdateTagDTO;
import com.example.CMCmp3.entity.Tag;
import com.example.CMCmp3.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    @Transactional(readOnly = true)
    public Page<TagDTO> getAllTags(Pageable pageable) {
        return tagRepository.findAll(pageable).map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public TagDTO getTagById(Long id) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thể loại với ID: " + id));
        return convertToDTO(tag);
    }

    @Transactional
    public TagDTO createTag(CreateTagDTO createTagDTO) {
        Optional<Tag> existingTag = tagRepository.findByName(createTagDTO.getName());
        if (existingTag.isPresent()) {
            throw new DataIntegrityViolationException("Tên thể loại '" + createTagDTO.getName() + "' đã tồn tại.");
        }
        Tag tag = convertToEntity(createTagDTO);
        Tag savedTag = tagRepository.save(tag);
        return convertToDTO(savedTag);
    }

    private TagDTO convertToDTO(Tag tag) {
        TagDTO dto = new TagDTO();
        dto.setId(tag.getId());
        dto.setName(tag.getName());
        dto.setDescription(tag.getDescription());
        return dto;
    }

    private Tag convertToEntity(CreateTagDTO dto) {
        Tag tag = new Tag();
        tag.setName(dto.getName());
        tag.setDescription(dto.getDescription());
        return tag;
    }
}
