package tis.service

import kotlin.concurrent.thread
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import tis.domain.AccountingBehavior

@Service
class ConsumerExecutor(
    private val accountFacadeService: ProduceService,
    private val consumerService: ConsumerService,
) {
    val log: Logger = LoggerFactory.getLogger(ConsumerExecutor::class.java)

    init {
        for (i in 0L..2L) {
            thread {
                while (true) {
                    try {
                        when (val behavior = accountFacadeService.consume(i)) {
                            is AccountingBehavior.Withdraw -> consumerService.withdraw(behavior)
                            is AccountingBehavior.Deposit -> consumerService.deposit(behavior)
                        }
                    } catch (e: Exception) {
                        log.error("Error: $e")
                    }
                }
            }
        }
    }
}
