package com.interview.task_manager.card;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

// [AI assisted chat-009] AI 協助定義經驗證的名片建立與更新請求。
public record CardRequest(
    @NotBlank @Size(max = 60) String name,
    @NotBlank @Size(max = 80) String role,
    @Size(max = 80) String company,
    @Email @Size(max = 254) String email,
    @Size(max = 40) String phone,
    @Size(max = 180) String website,
    @Size(max = 100) String location,
    @NotBlank @Size(max = 20) String color,
    List<String> displayFields,
    boolean published
) {
    public BusinessCard toEntity() {
        var card = new BusinessCard();
        card.setName(name.trim()); card.setRole(role.trim()); card.setCompany(clean(company));
        card.setEmail(clean(email)); card.setPhone(clean(phone)); card.setWebsite(clean(website));
        card.setLocation(clean(location)); card.setColor(color); card.setDisplayFields(displayFields); card.setPublished(published);
        return card;
    }
    private static String clean(String value) { return value == null || value.isBlank() ? null : value.trim(); }
}
