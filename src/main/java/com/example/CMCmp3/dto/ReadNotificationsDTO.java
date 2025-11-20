package com.example.CMCmp3.dto;

import lombok.Data;

import java.util.List;

@Data
public class ReadNotificationsDTO {
    private List<Long> notificationIds;
}
