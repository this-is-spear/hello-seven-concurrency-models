package tis.service

import org.springframework.stereotype.Service

@Service
class TransferService(
    private val depositService: DepositService,
    private val withdrawService: WithdrawService,
) {
    fun execute(fromUserId: Long, toUserId: Long, amount: Int) {
        withdrawService.execute(fromUserId, amount)
        try {
            depositService.execute(toUserId, amount)
        } catch (e: Exception) {
            depositService.execute(fromUserId, amount)
            throw e
        }
    }
}
