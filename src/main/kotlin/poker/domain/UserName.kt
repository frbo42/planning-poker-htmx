package poker.domain

import java.text.Collator
import java.util.Locale


private val collator = Collator.getInstance(Locale.GERMAN)

@JvmInline
value class UserName(private val name: String) : Comparable<UserName> {

    override fun compareTo(other: UserName): Int {
        return collator.compare(name, other.name)
    }

    override fun toString(): String {
        return name
    }
}