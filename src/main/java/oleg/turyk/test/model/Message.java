package oleg.turyk.test.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.SQLDelete;

@Entity
@Data
@SQLDelete(sql = "UPDATE messages SET is_deleted = true WHERE id=?")
@Filter(name = "deletedFilter", condition = "is_deleted = false")
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String prompt;
    @Column(nullable = false)
    private String response;
    @ElementCollection
    @CollectionTable(name = "message_admin_messages",
            joinColumns = @JoinColumn(name = "message_id"))
    @Column(name = "admin_message")
    private List<String> adminMessage = new ArrayList<>();
    @Column(nullable = false)
    private LocalDateTime timestamp;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id")
    private Chat chat;
    @Column(nullable = false)
    private boolean isDeleted = false;
}
