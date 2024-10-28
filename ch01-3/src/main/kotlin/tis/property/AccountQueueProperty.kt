package tis.property

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("account-queue")
class AccountQueueProperty(
    private val size: Long = 3L,
) {
    val range: LongRange
        get() = 0L until size
}
