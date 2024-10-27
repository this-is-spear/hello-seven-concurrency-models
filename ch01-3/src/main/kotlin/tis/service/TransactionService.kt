package tis.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import tis.domain.AccountTransaction
import tis.domain.AccountTransactionStatus
import tis.repository.AccountTransactionRepository

@Service
class TransactionService(
    private val accountTransactionRepository: AccountTransactionRepository,
) {
    fun isCompleted(transactionSequence: String): Boolean {
        while (getAccountTransaction(transactionSequence).status == AccountTransactionStatus.PENDING) {
            println("waiting for transaction: $transactionSequence")
            Thread.sleep(100)
        }
        return getAccountTransaction(transactionSequence).status == AccountTransactionStatus.COMPLETED
    }

    @Transactional
    fun start(transactionSequence: String) {
        val newAccountTransaction = AccountTransaction(transactionSequence)
        accountTransactionRepository.save(newAccountTransaction)
    }

    @Transactional
    fun complete(transactionSequence: String) {
        val accountTransaction = getAccountTransaction(transactionSequence)
        accountTransaction.status = AccountTransactionStatus.COMPLETED
        accountTransactionRepository.save(accountTransaction)
    }

    @Transactional
    fun fail(sequence: String) {
        val accountTransaction = getAccountTransaction(sequence)
        accountTransaction.status = AccountTransactionStatus.FAILED
        accountTransactionRepository.save(accountTransaction)
    }

    private fun getAccountTransaction(transactionSequence: String) =
        accountTransactionRepository.findByTransaction(transactionSequence)
            ?: throw IllegalArgumentException("No transaction data")

}
