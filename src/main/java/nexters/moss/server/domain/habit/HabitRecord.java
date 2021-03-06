package nexters.moss.server.domain.habit;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import nexters.moss.server.domain.TimeProvider;
import nexters.moss.server.domain.value.HabitStatus;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "habit_records")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HabitRecord extends TimeProvider {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "habit_record_id")
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "habit_id")
    private Long habitId;

    @Enumerated(EnumType.STRING)
    @Column(name = "habit_status")
    @Setter
    private HabitStatus habitStatus;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    @Column(name = "date")
    @Setter
    private LocalDateTime date;

    public boolean isDone() {
        if(this.habitStatus == HabitStatus.DONE || this.habitStatus == HabitStatus.CAKE_DONE) {
            return true;
        }
        return false;
    }

    public boolean isCakeDoneRecord() {
        if(this.habitStatus == HabitStatus.CAKE_DONE) {
            return true;
        }
        return false;
    }

    public void switchHabitStatus(HabitStatus habitStatus) {
        this.habitStatus = habitStatus;
    }
}
