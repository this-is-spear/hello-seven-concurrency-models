package tis.property

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("account-queue")
class AccountQueueProperty(
    val size: Long = 3L,
)
