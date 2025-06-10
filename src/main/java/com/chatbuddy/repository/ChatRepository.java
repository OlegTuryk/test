package com.chatbuddy.repository;

import java.util.Optional;
import com.chatbuddy.model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    @Query("FROM Chat c LEFT JOIN FETCH c.messages WHERE c.telegramChatId = :id")
    Optional<Chat> findByTelegramChatId(Long id);
}
