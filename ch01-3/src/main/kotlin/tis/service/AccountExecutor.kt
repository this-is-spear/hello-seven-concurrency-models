package tis.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import tis.domain.AccountingBehavior
import tis.property.AccountQueueProperty
import kotlin.concurrent.thread

@Service
class AccountExecutor(
    private val accountFacadeService: ProducerConsumerService,
    private val accountService: AccountService,
    private val transactionService: TransactionService,
    accountQueueProperty: AccountQueueProperty,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    init {
        for (i in 0L until accountQueueProperty.size) {
            thread {
                while (true) {
                    val behavior = accountFacadeService.consume(i)
                    try {
                        when (behavior) {
                            is AccountingBehavior.Withdraw -> accountService.withdraw(behavior)
                            is AccountingBehavior.Deposit -> accountService.deposit(behavior)
                        }
                        transactionService.complete(behavior.transactionSequence.sequence)
                    } catch (e: Exception) {
                        log.error("Failed to transaction", e)
                        transactionService.fail(behavior.transactionSequence.sequence)
                    }
                }
            }
        }
    }
}
