package oleg.turyk.test.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import oleg.turyk.test.dto.chat.ChatDetailsResponseDto;
import oleg.turyk.test.dto.chat.ChatResponseDto;
import oleg.turyk.test.dto.message.MessageDto;
import oleg.turyk.test.dto.message.MessageResponseDto;
import oleg.turyk.test.mapper.ChatMapper;
import oleg.turyk.test.mapper.MessageMapper;
import oleg.turyk.test.model.Chat;
import oleg.turyk.test.model.Message;
import oleg.turyk.test.repository.ChatRepository;
import oleg.turyk.test.repository.MessageRepository;
import oleg.turyk.test.service.impl.ChatServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class ChatServiceTest {
    private static final Chat CHAT = new Chat();
    private static final Message MESSAGE = new Message();
    private static final Long ID = 1L;
    @Mock
    private ChatRepository chatRepository;
    @Mock
    private MessageRepository messageRepository;
    @Mock
    private ChatMapper chatMapper;
    @Mock
    private MessageMapper messageMapper;
    @InjectMocks
    private ChatServiceImpl chatService;

    @BeforeEach
    void setUp() {
        CHAT.setId(ID);
        CHAT.setFirstName("Alice");
        CHAT.setUsername("alice123");
        CHAT.setTelegramChatId(123456789L);
        CHAT.setMessages(new ArrayList<>());

        MESSAGE.setId(ID);
        MESSAGE.setChat(CHAT);
        MESSAGE.setPrompt("Hello");
        MESSAGE.setResponse("Hi");
        MESSAGE.setAdminMessage(new ArrayList<>());
        MESSAGE.setTimestamp(LocalDateTime.of(2023, 1, 1, 1, 1, 1));

        CHAT.getMessages().add(MESSAGE);
    }

    @Test
    @DisplayName("Find chat")
    void findChat_validTelegramChat_returnValidChat() {
        org.telegram.telegrambots.meta.api.objects.Chat chat
                = new org.telegram.telegrambots.meta.api.objects.Chat();
        chat.setId(CHAT.getId());
        chat.setFirstName(CHAT.getFirstName());
        chat.setUserName(CHAT.getUsername());

        when(chatRepository.findByTelegramChatId(chat.getId())).thenReturn(Optional.of(CHAT));
        Chat actual = chatService.findChat(chat);
        assertEquals(actual, CHAT);
    }

    @Test
    @DisplayName("Create chat")
    void createChat_validTelegramChat_returnValidChat() {
        org.telegram.telegrambots.meta.api.objects.Chat chat
                = new org.telegram.telegrambots.meta.api.objects.Chat();
        chat.setId(CHAT.getId());
        chat.setFirstName(CHAT.getFirstName());
        chat.setUserName(CHAT.getUsername());

        when(chatRepository.findByTelegramChatId(chat.getId())).thenReturn(Optional.empty());
        when(chatRepository.save(any(Chat.class))).thenReturn(CHAT);
        Chat actual = chatService.findChat(chat);
        assertEquals(actual, CHAT);
    }

    @Test
    @DisplayName("Delete chat by id")
    public void deleteById_ChatExists_DeleteSuccessfully() {
        doNothing().when(chatRepository).deleteById(ID);
        chatService.deleteChat(ID);

        verify(chatRepository).deleteById(ID);
    }

    @Test
    @DisplayName("Delete message by id")
    public void deleteById_MessageExists_DeleteSuccessfully() {
        doNothing().when(messageRepository).deleteById(ID);
        chatService.deleteMessage(ID);

        verify(messageRepository).deleteById(ID);
    }

    @Test
    @DisplayName("Save message")
    void save_validMessage() {
        MessageResponseDto expected = new MessageResponseDto(MESSAGE.getId(),
                MESSAGE.getPrompt(), MESSAGE.getResponse(),
                MESSAGE.getAdminMessage(), MESSAGE.getTimestamp());

        when(messageRepository.save(MESSAGE)).thenReturn(MESSAGE);
        when(messageMapper.toDto(MESSAGE)).thenReturn(expected);
        MessageResponseDto actual = chatService.saveMessage(MESSAGE);

        assertEquals(actual, expected);
    }

    @Test
    @DisplayName("Get message history")
    public void getHistory_chatExists_returnMessages() {
        List<MessageDto> messageDtoList = new ArrayList<>();
        messageDtoList.add(new MessageDto("user", MESSAGE.getPrompt()));
        messageDtoList.add(new MessageDto("assistant", MESSAGE.getResponse()));

        when(chatRepository.findByTelegramChatId(CHAT
                .getTelegramChatId())).thenReturn(Optional.of(CHAT));

        List<MessageDto> actual = chatService.getHistory(CHAT.getTelegramChatId());
        assertEquals(actual, messageDtoList);
    }

    @Test
    @DisplayName("Get message history when chat doesn't exist")
    public void getHistory_chatDoesNotExist_returnEmptyList() {
        lenient().when(chatRepository.findByTelegramChatId(CHAT
                .getTelegramChatId())).thenReturn(Optional.empty());
        List<MessageDto> actual = chatService.getHistory(ID);
        List<MessageDto> expected = new ArrayList<>();
        assertEquals(actual, expected);
    }

    @Test
    @DisplayName("Get all chats")
    void getAll_validChats_returnChatList() {
        Pageable pageable = PageRequest.of(1, 2);
        Page<Chat> chatPage =
                new PageImpl<>(List.of(CHAT), pageable, 1);
        ChatResponseDto chatResponseDto = new ChatResponseDto(ID,
                CHAT.getTelegramChatId(), CHAT.getFirstName(), CHAT.getUsername());

        when(chatRepository.findAll(pageable)).thenReturn(chatPage);
        when(chatMapper.toDto(CHAT)).thenReturn(chatResponseDto);
        List<ChatResponseDto> actual = chatService.getChats(pageable);

        assertEquals(actual, List.of(chatResponseDto));
    }

    @Test
    @DisplayName("Get chat details")
    void getChatDetails_validId_returnChatDetails() {
        MessageResponseDto messageResponseDto = new MessageResponseDto(
                MESSAGE.getId(), MESSAGE.getPrompt(),
                MESSAGE.getResponse(), new ArrayList<>(),
                LocalDateTime.of(2023, 1, 1, 1, 1, 1));
        ChatDetailsResponseDto chatDetailsResponseDto =
                new ChatDetailsResponseDto(ID,
                        CHAT.getTelegramChatId(), CHAT.getFirstName(),
                        CHAT.getUsername(), List.of(messageResponseDto));

        when(chatRepository.findById(ID)).thenReturn(Optional.of(CHAT));
        when(chatMapper.toDetailsDto(CHAT)).thenReturn(chatDetailsResponseDto);

        ChatDetailsResponseDto actual = chatService.getChatDetails(ID);
        assertEquals(actual, chatDetailsResponseDto);
    }

    @Test
    @DisplayName("Get chat details when chat doesn't exist")
    void getChatDetails_invalidId_throwException() {
        when(chatRepository.findById(ID)).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> chatService.getChatDetails(ID));
        assertEquals("Chat with id " + ID + " doesn't exist", exception.getMessage());
    }

    @Test
    @DisplayName("Get last message in chat")
    void getLastChatMessage_validTelegramId_returnMessage() {
        when(chatRepository.findByTelegramChatId(CHAT
                .getTelegramChatId())).thenReturn(Optional.of(CHAT));

        Message actual = chatService.findLastMessageByTelegramChatId(CHAT.getTelegramChatId());
        assertEquals(actual, MESSAGE);
    }

    @Test
    @DisplayName("Get last message when chat doesn't exist")
    void getLastChatMessage_invalidTelegramId_throwException() {
        when(chatRepository.findByTelegramChatId(CHAT
                .getTelegramChatId())).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> chatService.findLastMessageByTelegramChatId(CHAT.getTelegramChatId()));
        assertEquals("Chat with id " + CHAT.getTelegramChatId() + " doesn't exist",
                exception.getMessage());
    }
}
