package tis.service

import kotlin.concurrent.thread
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import tis.domain.AccountingBehavior

@Service
class AccountExecutor(
    private val accountFacadeService: ProducerConsumerService,
    private val accountService: AccountService,
) {
    val log: Logger = LoggerFactory.getLogger(AccountExecutor::class.java)

    init {
        for (i in 0L..2L) {
            thread {
                while (true) {
                    try {
                        when (val behavior = accountFacadeService.consume(i)) {
                            is AccountingBehavior.Withdraw -> accountService.withdraw(behavior)
                            is AccountingBehavior.Deposit -> accountService.deposit(behavior)
                        }
                    } catch (e: Exception) {
                        log.error("Error: $e")
                    }
                }
            }
        }
    }
}
