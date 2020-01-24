fun test(elements: Array<out String?>) {
    val filtered = elements.<!INAPPLICABLE_CANDIDATE("[kotlin/collections/filterNotNull,kotlin/collections/filterNotNull,kotlin/sequences/filterNotNull]")!>filterNotNull<!>()
}
