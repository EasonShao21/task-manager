package com.interview.task_manager.card;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
public class CardDemoData {
    @Bean
    CommandLineRunner seedCards(CardRepository cards) {
        return args -> {
            if (cards.count() > 0) return;
            cards.saveAll(List.of(
                card("林予安", "Product Designer", "Studio North", "hello@yuanlin.design", "+886 912 345 678", "yuanlin.design", "Taipei, Taiwan", "violet", true),
                card("Yuan Lin", "Frontend Developer", "Independent", "dev@yuanlin.design", "+886 912 345 678", "github.com/yuanlin", "Taipei, Taiwan", "mint", true),
                card("林予安", "Creative Practice", "Personal", "studio@yuanlin.design", null, "yuanlin.design/notes", "Taipei, Taiwan", "sand", false)
            ));
        };
    }
    private BusinessCard card(String name, String role, String company, String email, String phone, String website, String location, String color, boolean published) {
        var card = new BusinessCard();
        card.setName(name); card.setRole(role); card.setCompany(company); card.setEmail(email); card.setPhone(phone);
        card.setWebsite(website); card.setLocation(location); card.setColor(color);
        card.setDisplayFields(List.of("email", "phone", "website")); card.setPublished(published);
        return card;
    }
}
