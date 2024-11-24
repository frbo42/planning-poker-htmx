package poker.infrastructure.clean

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import poker.application.GameService
import java.util.concurrent.TimeUnit

@Component
class CleanTask (private val gameService: GameService){

    @Scheduled(fixedRate = 20, timeUnit = TimeUnit.SECONDS)
    fun clean(){
        gameService.clean()
    }
}