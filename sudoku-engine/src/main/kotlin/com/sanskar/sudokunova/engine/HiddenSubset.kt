package com.sanskar.sudokunova.engine

internal data class HiddenSubsetMatch(
    val sourceCells: List<Int>,
    val values: Set<Int>,
    val eliminations: List<CandidateElimination>,
)

internal fun findHiddenSubset(
    unitIndices: List<Int>,
    candidates: Map<Int, Set<Int>>,
    subsetSize: Int,
): HiddenSubsetMatch? {
    require(subsetSize in 2..4)

    val occurrences = (1..9).associateWith { value ->
        unitIndices.filter { index -> value in candidates[index].orEmpty() }
    }
    val eligibleValues = (1..9).filter { value ->
        occurrences.getValue(value).size in 2..subsetSize
    }
    if (eligibleValues.size < subsetSize) return null

    for (valuesList in hiddenCombinations(eligibleValues, subsetSize)) {
        val values = valuesList.toSortedSet()
        val sourceCells = valuesList
            .flatMap { value -> occurrences.getValue(value) }
            .distinct()
            .sorted()
        if (sourceCells.size != subsetSize) continue

        val eliminations = buildList {
            sourceCells.forEach { index ->
                candidates[index].orEmpty()
                    .filterNot { it in values }
                    .sorted()
                    .forEach { value -> add(CandidateElimination(index, value)) }
            }
        }
        if (eliminations.isNotEmpty()) {
            return HiddenSubsetMatch(
                sourceCells = sourceCells,
                values = values,
                eliminations = eliminations,
            )
        }
    }
    return null
}

private fun <T> hiddenCombinations(
    values: List<T>,
    size: Int,
): Sequence<List<T>> = sequence {
    require(size >= 0)
    if (size == 0) {
        yield(emptyList())
        return@sequence
    }
    if (values.size < size) return@sequence

    val indices = IntArray(size) { it }
    while (true) {
        yield(indices.map(values::get))

        var pivot = size - 1
        while (pivot >= 0 && indices[pivot] == values.size - size + pivot) pivot--
        if (pivot < 0) break
        indices[pivot]++
        for (position in pivot + 1 until size) {
            indices[position] = indices[position - 1] + 1
        }
    }
}
