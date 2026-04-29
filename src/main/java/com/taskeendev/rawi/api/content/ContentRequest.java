package com.taskeendev.rawi.api.content;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ContentRequest(
        @NotBlank(message = "URL is required")
        @Pattern(regexp = "https?://.+", message = "URL must start with http:// or https://")
        String url,

        String title,
        String content,
        String source
) {}
