package com.week04.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Pet {
    private Long id;

    private Category category;

    private String name;

    @JsonProperty("photoUrls")
    private List<String> photoUrls;

    private List<Tag> tags;

    private String status;  // available, pending, sold
}