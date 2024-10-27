package tis.service

import org.springframework.stereotype.Service
import tis.domain.AccountQueue
import tis.domain.AccountingBehavior

@Service
class ProducerConsumerService {
    private val map: Map<Long, AccountQueue> = mapOf(
        0L to AccountQueue(0L),
        1L to AccountQueue(1L),
        2L to AccountQueue(2L),
    )

    fun produce(item: AccountingBehavior) {
        val shardingKey = getShardingKey(item.member.id)
        val accountService = map[shardingKey]!!
        accountService.produce(item)
    }

    fun consume(shardingKey: Long): AccountingBehavior {
        val accountService = map[shardingKey]!!
        return accountService.consume()
    }

    private fun getShardingKey(memberId: Long): Long {
        return memberId % 3
    }
}
