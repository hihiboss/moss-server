package nexters.moss.server.domain.model;

import lombok.*;
import nexters.moss.server.application.dto.cake.CreateNewCakeRequest;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sent_piece_of_cakes")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SentPieceOfCake extends TimeProvider {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sent_piece_of_cake_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "note")
    @Length(min = 3, max = 14)
    private String note;

    @OneToMany(mappedBy = "sentPieceOfCake", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReceivedPieceOfCake> receivedPieceOfCakeList = new ArrayList<>();

    public SentPieceOfCake(Long userId, CreateNewCakeRequest createNewCakeRequest) {
        this.user = User.builder()
                .id(userId)
                .build();
        this.category = Category.builder()
                .id(createNewCakeRequest.getCategoryId())
                .build();
        this.note = createNewCakeRequest.getNote();
    }
}
