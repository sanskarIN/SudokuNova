package com.sanskar.sudokunova.engine

internal data class NakedSubsetMatch(
    val sourceCells: List<Int>,
    val values: Set<Int>,
    val eliminations: List<CandidateElimination>,
)

internal fun findNakedSubset(
    unitIndices: List<Int>,
    candidates: Map<Int, Set<Int>>,
    subsetSize: Int,
): NakedSubsetMatch? {
    require(subsetSize in 2..4)

    val eligible = unitIndices.filter { index ->
        candidates[index]?.size in 2..subsetSize
    }
    if (eligible.size < subsetSize) return null

    for (sourceCells in combinations(eligible, subsetSize)) {
        val values = sourceCells
            .flatMap { candidates[it].orEmpty() }
            .toSortedSet()
        if (values.size != subsetSize) continue

        val sourceSet = sourceCells.toSet()
        val eliminations = buildList {
            unitIndices.forEach { index ->
                if (index in sourceSet) return@forEach
                val cellCandidates = candidates[index].orEmpty()
                values.forEach { value ->
                    if (value in cellCandidates) add(CandidateElimination(index, value))
                }
            }
        }
        if (eliminations.isNotEmpty()) {
            return NakedSubsetMatch(
                sourceCells = sourceCells,
                values = values,
                eliminations = eliminations,
            )
        }
    }
    return null
}

private fun <T> combinations(
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
