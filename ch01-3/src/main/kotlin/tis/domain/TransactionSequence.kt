package tis.domain

import java.time.Instant
import java.util.UUID

private const val PARTITION = "-"

data class TransactionSequence(
    val instant: Instant,
    val uuid: UUID,
    val serverId: String = "tis-01",
) {
    val sequence: String
        get() = listOf(instant, serverId, uuid).joinToString(PARTITION)
}
