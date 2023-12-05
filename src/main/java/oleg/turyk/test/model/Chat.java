package oleg.turyk.test.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.SQLDelete;

@Entity
@Data
@SQLDelete(sql = "UPDATE chats SET is_deleted = true WHERE id=?")
@Filter(name = "deletedFilter", condition = "is_deleted = false")
@Table(name = "chats")
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private Long telegramChatId;
    private String firstName;
    private String username;
    @OneToMany(mappedBy = "chat")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Message> messages;
    @Column(nullable = false)
    private boolean isDeleted = false;
}
