package com.interview.task_manager.card;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

// [AI assisted chat-009] AI 協助實作 RESTful CRUD 與 OpenAPI 註解。
@RestController
@RequestMapping("/api/cards")
@CrossOrigin(originPatterns = {"http://localhost:*", "http://127.0.0.1:*", "http://192.168.*.*:*", "http://10.*.*.*:*", "http://172.*.*.*:*"})
@Tag(name = "名片", description = "建立、查詢、更新與刪除數位名片")
public class CardController {
    private final CardRepository cards;
    public CardController(CardRepository cards) { this.cards = cards; }

    @GetMapping @Operation(summary = "列出名片")
    public List<BusinessCard> list() { return cards.findAll(); }

    @GetMapping("/{id}") @Operation(summary = "取得單張名片")
    public BusinessCard get(@PathVariable Long id) {
        var card = cards.findById(id).orElseThrow(() -> new CardNotFoundException(id));
        if (!card.isPublished()) throw new CardNotFoundException(id);
        return card;
    }

    @PostMapping @ResponseStatus(HttpStatus.CREATED) @Operation(summary = "建立名片")
    public BusinessCard create(@Valid @RequestBody CardRequest request) { return cards.save(request.toEntity()); }

    @PutMapping("/{id}") @Operation(summary = "修改名片與外觀設定")
    public BusinessCard update(@PathVariable Long id, @Valid @RequestBody CardRequest request) {
        var card = cards.findById(id).orElseThrow(() -> new CardNotFoundException(id));
        var replacement = request.toEntity();
        replacement.setId(card.getId()); replacement.setCreatedAt(card.getCreatedAt()); replacement.setUpdatedAt(card.getUpdatedAt());
        return cards.save(replacement);
    }

    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) @Operation(summary = "刪除名片")
    public void delete(@PathVariable Long id) {
        if (!cards.existsById(id)) throw new CardNotFoundException(id);
        cards.deleteById(id);
    }
}
