package com.interview.task_manager.card;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

// [AI assisted chat-009] AI 協助建立名片領域模型與持久化欄位。
@Entity
@Table(name = "business_cards")
public class BusinessCard {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, length = 60) private String name;
    @Column(nullable = false, length = 80) private String role;
    @Column(length = 80) private String company;
    @Column(length = 254) private String email;
    @Column(length = 40) private String phone;
    @Column(length = 180) private String website;
    @Column(length = 100) private String location;
    @Column(nullable = false, length = 20) private String color = "violet";
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "business_card_display_fields", joinColumns = @JoinColumn(name = "card_id"))
    @Column(name = "field_name", nullable = false, length = 30)
    private List<String> displayFields = new ArrayList<>(List.of("email", "phone", "website"));
    @Column(nullable = false) private boolean published = true;
    @Column(nullable = false, updatable = false) private Instant createdAt = Instant.now();
    @Column(nullable = false) private Instant updatedAt = Instant.now();

    @PreUpdate void touch() { updatedAt = Instant.now(); }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public List<String> getDisplayFields() { return displayFields; }
    public void setDisplayFields(List<String> displayFields) { this.displayFields = displayFields == null ? new ArrayList<>() : new ArrayList<>(displayFields); }
    public boolean isPublished() { return published; }
    public void setPublished(boolean published) { this.published = published; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
