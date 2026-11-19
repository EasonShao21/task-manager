package com.interview.task_manager.card;

public class CardNotFoundException extends RuntimeException {
    public CardNotFoundException(Long id) { super("名片不存在: " + id); }
}
