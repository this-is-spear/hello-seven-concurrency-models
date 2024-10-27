package tis.service

import kotlin.concurrent.thread
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import tis.domain.AccountingBehavior

@Service
class RecordExecutor(
    private val accountService: AccountService,
    private val recordService: RecordService,
) {
    val log: Logger = LoggerFactory.getLogger(RecordExecutor::class.java)

    init {
        thread {
            while (true) {
                try {
                    when (val behavior = accountService.consume()) {
                        is AccountingBehavior.Withdraw -> recordService.withdraw(behavior)
                        is AccountingBehavior.Deposit -> recordService.deposit(behavior)
                    }
                } catch (e: Exception) {
                    log.error("Error: $e")
                }
            }
        }
    }
}
