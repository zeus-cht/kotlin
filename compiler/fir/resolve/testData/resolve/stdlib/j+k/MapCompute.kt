// FULL_JDK

fun <D> MutableMap<String, MutableSet<D>>.initAndAdd(key: String, value: D) {
    this.<!UNRESOLVED_REFERENCE!>compute<!>(key) { _, maybeValues ->
        val setOfValues = maybeValues ?: mutableSetOf()
        setOfValues.<!UNRESOLVED_REFERENCE!>add<!>(value)
        setOfValues
    }
}