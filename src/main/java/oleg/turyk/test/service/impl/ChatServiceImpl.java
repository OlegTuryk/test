package oleg.turyk.test.service.impl;

import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import oleg.turyk.test.dto.chat.ChatDetailsResponseDto;
import oleg.turyk.test.dto.chat.ChatResponseDto;
import oleg.turyk.test.dto.message.MessageDto;
import oleg.turyk.test.mapper.ChatMapper;
import oleg.turyk.test.model.Chat;
import oleg.turyk.test.model.Message;
import oleg.turyk.test.repository.ChatRepository;
import oleg.turyk.test.repository.MessageRepository;
import oleg.turyk.test.service.ChatService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    private static final int MAX_MESSAGES = 20;
    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final ChatMapper chatMapper;

    @Override
    public void saveMessage(Message message) {
        messageRepository.save(message);
    }

    @Override
    public Chat findChat(org.telegram.telegrambots.meta.api.objects.Chat chat) {
        return chatRepository.findByTelegramChatId(chat.getId())
                .orElseGet(() -> createChat(chat.getId(),
                        chat.getFirstName(), chat.getUserName()));
    }

    @Override
    public List<MessageDto> getHistory(Long chatId) {
        Chat chat = chatRepository.findByTelegramChatId(chatId).orElse(null);
        if (chat == null) {
            return new ArrayList<>();
        }
        List<Message> messages = getLastMessages(chat.getMessages());
        return messages.stream()
                .flatMap(message -> Stream.of(
                        new MessageDto("user", message.getPrompt()),
                        new MessageDto("assistant", message.getResponse())
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<ChatResponseDto> getChats(Pageable pageable) {
        return chatRepository.findAll(pageable).stream()
                .map(chatMapper::toDto)
                .toList();
    }

    @Override
    public ChatDetailsResponseDto getChatDetails(Long id) {
        return chatMapper.toDetailsDto(chatRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Chat with id "
                        + id + " doesn't exist")));
    }

    @Override
    public void deleteChat(Long id) {
        chatRepository.deleteById(id);
    }

    @Override
    public void deleteMessage(Long id) {
        messageRepository.deleteById(id);
    }

    @Override
    public Message findLastMessageByTelegramChatId(Long telegramChatId) {
        Chat chat = chatRepository.findByTelegramChatId(telegramChatId)
                .orElseThrow(() -> new EntityNotFoundException("Chat with id "
                        + telegramChatId + " doesn't exist"));
        List<Message> messages = chat.getMessages();
        return messages.get(messages.size() - 1);
    }

    private Chat createChat(Long telegramChatId, String firstName, String username) {
        Chat chat = new Chat();
        chat.setTelegramChatId(telegramChatId);
        chat.setFirstName(firstName);
        chat.setUsername(username);
        chat.setMessages(new ArrayList<>());
        return chatRepository.save(chat);
    }

    private List<Message> getLastMessages(List<Message> messages) {
        int start = Math.max(0, messages.size() - MAX_MESSAGES);
        return messages.subList(start, messages.size());
    }
}
