package day09

import util.println
import util.readInput
import kotlin.time.measureTime

private const val folder = "day09"

fun main() {

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("$folder/test")
    check(part1(testInput) == 1928L)
    check(part2(testInput) == 2858L)

    val input = readInput("$folder/input")
    measureTime {
        part1(input).println()
    }.println()
    measureTime {
        part2(input).println()
    }.println()
}

private fun part1(input: List<String>): Long {
    val diskMap = input.first().map { it.digitToInt() }
    val disk = createDisk(diskMap)
    return compactDisk(disk).mapIndexedNotNull { index, fileId ->
        fileId?.times(index)
    }.sum()
}

private fun createDisk(diskMap: List<Int>): Array<Long?> {
    val disk = Array<Long?>(diskMap.sum()) { null }
    var i = 0
    var isData = true
    var fileId = 0L
    diskMap.forEach { entry ->
        if (isData) {
            (1..entry).forEach {
                disk[i] = fileId
                i++
            }
            fileId++
        } else {
            i += entry
        }
        isData = !isData
    }
    return disk
}

private fun compactDisk(disk: Array<Long?>): Array<Long?> {
    var indexFront = 0
    var indexBack = disk.lastIndex
    while (indexFront < indexBack) {
        if (disk[indexFront] == null) {
            if (disk[indexBack] == null) {
                indexBack--
            } else {
                disk[indexFront] = disk[indexBack]
                disk[indexBack] = null
            }
        } else {
            indexFront++
        }
    }
    return disk
}

private fun part2(input: List<String>): Long {
    val diskMap = createMap(input).toMutableList()
    val compressedMap = compressMap(diskMap)
    return calculateChecksum(compressedMap)
}

private fun createMap(input: List<String>): List<DiskMapEntry> {
    var isFile = true
    var fileId = 0
    return input.first().map { it.digitToInt() }
        .map {
            val entry = if (isFile) {
                DiskMapEntry.File(it, fileId++)
            } else {
                DiskMapEntry.FreeSpace(it)
            }
            isFile = !isFile
            entry
        }
}

private fun compressMap(diskMap: MutableList<DiskMapEntry>): List<DiskMapEntry> {
    val files = diskMap.filterIsInstance<DiskMapEntry.File>().reversed()
    files.forEach { file ->
        var index = 0
        while (diskMap[index] != file) {
            val entry = diskMap[index]
            if (entry is DiskMapEntry.FreeSpace && entry.size >= file.size) {
                if (file.size == entry.size) {
                    diskMap[index] = file
                } else {
                    diskMap[index] = entry.copy(size = entry.size - file.size)
                    diskMap.add(index, file)
                }
                // remove file from end
                val toRemove = diskMap.lastIndexOf(file)
                val removeStartIndex = diskMap.getOrNull(toRemove - 1).takeIf { it is DiskMapEntry.FreeSpace }?.let { toRemove - 1 } ?: toRemove
                val removeEndIndex = diskMap.getOrNull(toRemove + 1).takeIf { it is DiskMapEntry.FreeSpace }?.let { toRemove + 1 } ?: toRemove
                val size = (removeStartIndex..removeEndIndex).sumOf { diskMap[it].size }
                (removeEndIndex downTo removeStartIndex).forEach { diskMap.removeAt(it) }
                diskMap.add(removeStartIndex, DiskMapEntry.FreeSpace(size))
                break
            }
            index++
        }
    }

    return diskMap
}

private fun calculateChecksum(diskMap: List<DiskMapEntry>): Long {
    var index = 0
    return diskMap.sumOf { entry ->
        when(entry) {
            is DiskMapEntry.File -> {
                (0..<entry.size).sumOf {
                    (index++ * entry.fileId).toLong()
                }
            }
            is DiskMapEntry.FreeSpace -> {
                index += entry.size
                0L
            }
        }
    }
}

private sealed class DiskMapEntry {

    abstract val size: Int

    data class File(
        override val size: Int,
        val fileId: Int
    ) : DiskMapEntry()

    data class FreeSpace(
        override val size: Int
    ) : DiskMapEntry()
}
