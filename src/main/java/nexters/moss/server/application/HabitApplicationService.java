package nexters.moss.server.application;

import nexters.moss.server.application.dto.HabitHistory;
import nexters.moss.server.application.dto.Response;
import nexters.moss.server.domain.model.Habit;
import nexters.moss.server.domain.model.User;
import nexters.moss.server.domain.repository.HabitRecordRepository;
import nexters.moss.server.domain.repository.HabitRepository;
import nexters.moss.server.domain.repository.UserRepository;
import nexters.moss.server.domain.service.HabitRecordService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class HabitApplicationService {
    private HabitRepository habitRepository;
    private HabitRecordRepository habitRecordRepository;
    private HabitRecordService habitRecordService;
    private UserRepository userRepository;

    public HabitApplicationService(
            HabitRepository habitRepository,
            HabitRecordRepository habitRecordRepository,
            HabitRecordService habitRecordService,
            UserRepository userRepository
    ) {
        this.habitRepository = habitRepository;
        this.habitRecordRepository = habitRecordRepository;
        this.habitRecordService = habitRecordService;
        this.userRepository = userRepository;
    }

    public Response<HabitHistory> createHabit(Long userId, Long habitId) {
        Habit habit = habitRepository.findById(habitId).orElseThrow(() -> new IllegalArgumentException("No Matched HabitType"));
        return new Response(
                new HabitHistory(
                        habit.getId(),
                        habit.getCategory().getHabitType(),
                        habitRecordRepository.saveAll(
                                habitRecordService.createHabitRecords(userId, habitId)
                        )
                )
        );
    }

    public Response<List<HabitHistory>> getHabitHistory(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("No Matched User"));
        return new Response<>(
                user.getHabits()
                        .stream()
                        .map(habit -> new HabitHistory(
                                        habit.getId(),
                                        habit.getCategory().getHabitType(),
                                        habitRecordService.validateAndRefreshHabitHistory(habit.getHabitRecords())
                                )
                        )
                        .collect(Collectors.toList())
        );
    }

    public Response<Long> deleteHabit(Long userId, Long habitId) {
        habitRecordRepository.deleteAllByUser_IdAndHabit_Id(userId, habitId);
        return new Response<>(habitId);
    }
}
