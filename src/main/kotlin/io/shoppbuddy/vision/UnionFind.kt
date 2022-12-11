package io.shoppbuddy.vision

interface UnionFindable {
    var id: Int
}

class UnionFind<T : UnionFindable>(size: Int) {
    private val ids: Array<Int> = Array(size) { index -> index }
    private val ranks: Array<Int> = Array(size) { 0 }

    private fun find(id: Int): Int {
        if (ids[id] != id)
            ids[id] = find(ids[id])
        return ids[id]
    }

    fun union(first: T, second: T, isLinkable: (first: T, second: T) -> Boolean) {
        val pFirst = find(first.id)
        val pSecond = find(second.id)
        if (pFirst == pSecond) return

        if (isLinkable.invoke(first, second)) {
            if (ranks[pFirst] > ranks[pSecond]) {
                second.id = pFirst
            } else {
                first.id = pSecond
                if (ranks[pFirst] == ranks[pSecond])
                    ranks[pSecond]++
            }
        }
    }
}