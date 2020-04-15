package nexters.moss.server;


import nexters.moss.server.domain.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Configuration
public class TestConfiguration {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HabitRepository habitRepository;

    @Autowired
    private SentPieceOfCakeRepository sentPieceOfCakeRepository;

    @Autowired
    private ReceivedPieceOfCakeRepository receivedPieceOfCakeRepository;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private WholeCakeRepository wholeCakeRepository;

    public void tearDown() {
        receivedPieceOfCakeRepository.deleteAll();
        sentPieceOfCakeRepository.deleteAll();
        wholeCakeRepository.deleteAll();
        habitRepository.deleteAll();
        reportRepository.deleteAll();
        userRepository.deleteAll();
    }
}
