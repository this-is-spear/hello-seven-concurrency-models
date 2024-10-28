package tis.service

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
    init {
        for (i in 0L..<accountQueueProperty.queueSize) {
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
                        transactionService.fail(behavior.transactionSequence.sequence)
                    }
                }
            }
        }
    }
}
