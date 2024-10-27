package tis.domain

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class AccountTransaction(
    val transaction: String,
    @Enumerated(EnumType.STRING)
    var status: AccountTransactionStatus = AccountTransactionStatus.PENDING,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
)
