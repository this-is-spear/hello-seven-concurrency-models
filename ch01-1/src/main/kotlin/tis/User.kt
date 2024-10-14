package tis

data class User(
    val name: String,
    var balance: Money = Money(),
) {
    operator fun compareTo(target: User): Int {
        if (name == target.name) {
            return name.hashCode().compareTo(target.name.hashCode())
        }
        return name.compareTo(target.name)
    }
}
