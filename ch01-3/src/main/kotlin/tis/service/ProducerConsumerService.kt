package tis.service

import org.springframework.stereotype.Service
import tis.domain.AccountQueue
import tis.domain.AccountingBehavior
import tis.property.AccountQueueProperty

@Service
class ProducerConsumerService(
    private val accountQueueProperty: AccountQueueProperty,
) {
    val map: Map<Long, AccountQueue> by lazy {
        val map = mutableMapOf<Long, AccountQueue>()
        for (i in 0L until accountQueueProperty.size) {
            map[i] = AccountQueue(i)
        }
        map.toMap()
    }

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
