package tis.repository

import org.springframework.data.jpa.repository.JpaRepository
import tis.domain.AccountTransaction

interface AccountTransactionRepository: JpaRepository<AccountTransaction, Long> {
    fun findByTransaction(transaction: String): AccountTransaction?
}

