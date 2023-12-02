package oleg.turyk.test.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import oleg.turyk.test.bot.ChatBot;
import oleg.turyk.test.dto.chat.ChatDetailsResponseDto;
import oleg.turyk.test.dto.chat.ChatResponseDto;
import oleg.turyk.test.dto.message.WriteToUserRequestDto;
import oleg.turyk.test.service.ChatService;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Chats management", description = "Endpoints for managing chats and messages")
@RequiredArgsConstructor
@RestController
@RequestMapping("/chats")
public class ChatController {
    private final ChatService chatService;
    private final ChatBot chatBot;

    @Operation(summary = "Get all chats",
            description = "Return a list of all chats")
    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<ChatResponseDto> getChats(Pageable pageable) {
        return chatService.getChats(pageable);
    }

    @Operation(summary = "Write a message to a user in a telegram chat",
            description = "Allows the administrator to write a message"
                    + " to a user in a telegram chat. "
                    + "The chat must exist and the telegram chat ID must match")
    @PostMapping("/{telegramChatId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void writeToUser(@PathVariable Long telegramChatId,
                            @RequestBody @Valid WriteToUserRequestDto write) {
        chatBot.writeToUser(telegramChatId, write);
    }

    @Operation(summary = "Get chat details by ID",
            description = "Returns the details of a chat by its ID."
                    + " The chat must exist and the ID must match")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ChatDetailsResponseDto getChatDetails(@PathVariable Long id) {
        return chatService.getChatDetails(id);
    }

    @Operation(summary = "Delete a chat by ID",
            description = "Deletes a chat by its ID."
                    + " The chat must exist and the ID must match")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void deleteChat(@PathVariable Long id) {
        chatService.deleteChat(id);
    }

    @Operation(summary = "Delete a message by ID",
            description = "Deletes a message by its ID."
                    + " The message must exist and the ID must match")
    @DeleteMapping("/messages/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void deleteMessage(@PathVariable Long id) {
        chatService.deleteMessage(id);
    }
}
