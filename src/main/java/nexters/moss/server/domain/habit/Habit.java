package nexters.moss.server.domain.habit;

import lombok.*;
import nexters.moss.server.domain.TimeProvider;
import nexters.moss.server.domain.value.HabitStatus;
import org.hibernate.annotations.OrderBy;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "habits",
        indexes = {
                @Index(name = "habit_user_id", columnList = "user_id")
        })
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Habit extends TimeProvider {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "habit_id")
    private Long id;

    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "user_id")
    private Long userId;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy(clause = "id ASC")
    @JoinColumn(name = "habit_id")
    private List<HabitRecord> habitRecords = new ArrayList<>();

    @Column(name = "habit_order")
    private Integer order = 0;

    @Column(name = "isActivated", columnDefinition = "boolean default false")
    private Boolean isActivated = false;

    public Habit(Long id) {
        this.id = id;
    }

    @Builder
    public Habit(Long categoryId, Long userId) {
        this.categoryId = categoryId;
        this.userId = userId;
        this.habitRecords = new ArrayList<>();
        this.order = 0;
        this.isActivated = false;
        createHabitRecords(userId);
    }

    public void createHabitRecords(Long userId) {
        LocalDateTime date = LocalDate.now().minusDays(1).atTime(0, 0, 0);
        List<HabitRecord> habitRecords = new ArrayList<>();
        for (int day = 0; day < 5; day++) {
            HabitStatus habitStatus = HabitStatus.NOT_DONE;
            // 당일을 기점으로 3일 뒤에는 cake를 받을 수 있음.
            if (day == 3) {
                habitStatus = HabitStatus.CAKE_NOT_DONE;
            }
            habitRecords.add(
                    HabitRecord.builder()
                            .userId(userId)
                            .habitId(this.id)
                            .habitStatus(habitStatus)
                            .date(date.plusDays(day))
                            .build()
            );
        }
        this.habitRecords = habitRecords;
    }

    public boolean isToday() {
        int today = LocalDateTime.now().getDayOfMonth();
        int recordToday = this.habitRecords.get(1).getDate().getDayOfMonth();
        return today == recordToday;
    }

    public void onActivation() {
        this.isActivated = true;
    }

    public boolean isReadyToReceiveCake() {
        HabitRecord todayRecord = this.habitRecords.get(1);
        return todayRecord.isCakeDoneRecord();
    }

    public boolean isTodayDone() {
        HabitRecord todayRecord = this.habitRecords.get(1);
        return todayRecord.isDone();
    }

    public void todayDone() {
        HabitRecord todayRecord = this.habitRecords.get(1);
        switch (todayRecord.getHabitStatus()) {
            case NOT_DONE:
                todayRecord.switchHabitStatus(HabitStatus.DONE);
                break;
            case CAKE_NOT_DONE:
                todayRecord.switchHabitStatus(HabitStatus.CAKE_DONE);
                break;
        }
    }

    public void changeHabitRecords(List<HabitRecord> habitRecords) {
        this.habitRecords = habitRecords;
    }

    public void increaseOneOrder() {
        this.order++;
    }

    public void decreaseOneOrder() {
        this.order--;
    }

    public void changeOrder(int order) {
        this.order = order;
    }

    public void refreshHabitHistory() {
        if(this.habitRecords.size() != 5) {
            // TODO: throw exceptions
        }
        // TODO: check sorted right
        int today = LocalDateTime.now().getDayOfMonth();
        int recordToday = this.habitRecords.get(1).getDate().getDayOfMonth();
        HabitStatus currentHabitStatus =this.habitRecords.get(1).getHabitStatus();

        if (today != recordToday) {
            switch (currentHabitStatus) {
                case CAKE_NOT_DONE:
                case NOT_DONE:
                    resetHabitRecords(this.habitRecords);
                    break;
                case DONE:
                case CAKE_DONE:
                    refreshHabitRecords(this.habitRecords);
                    break;
            }
        }
    }

    private void resetHabitRecords(List<HabitRecord> habitRecords) {
        LocalDateTime today = LocalDate.now().atTime(0, 0, 0);
        for (int day = 0; day < 5; day++) {
            HabitStatus habitStatus = HabitStatus.NOT_DONE;
            if (day == 3) {
                habitStatus = HabitStatus.CAKE_NOT_DONE;
            }

            if (day == 4) {
                habitRecords.get(day).setDate(
                        today.plusDays(3)
                );
                habitRecords.get(day).setHabitStatus(habitStatus);
                continue;
            }

            habitRecords.get(day).setDate(
                    today.plusDays(day - 1)
            );
            habitRecords.get(day).setHabitStatus(habitStatus);
        }
    }

    private void refreshHabitRecords(List<HabitRecord> habitRecords) {
        LocalDateTime today = LocalDate.now().atTime(0, 0, 0);
        HabitStatus yesterdayHabitStatus = habitRecords.get(0).getHabitStatus();
        HabitRecord lastHabitRecord = habitRecords.get(4);
        for (int day = 0; day < 5; day++) {
            if (day == 4) {
                if(yesterdayHabitStatus == HabitStatus.DONE) {
                    if (lastHabitRecord.getHabitStatus() == HabitStatus.CAKE_NOT_DONE) {
                        lastHabitRecord.setHabitStatus(HabitStatus.NOT_DONE);
                    } else if (lastHabitRecord.getHabitStatus() == HabitStatus.NOT_DONE) {
                        lastHabitRecord.setHabitStatus(HabitStatus.CAKE_NOT_DONE);
                    }
                }

                habitRecords.get(day).setDate(
                        today.plusDays(3)
                );
                continue;
            }
            habitRecords.get(day).setHabitStatus(
                    habitRecords.get(day + 1).getHabitStatus()
            );
            habitRecords.get(day).setDate(
                    today.plusDays(day - 1)
            );
        }
    }
}
