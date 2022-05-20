package com.tpp.shortner.service

import org.springframework.stereotype.Service
import kotlin.math.pow

@Service
class KeyConverter(private val alphaBet: List<Char> = ('A'..'z').toList()) {
    private val alphaBetSize = alphaBet.size.toULong()

    fun convert(idx: ULong): String {
        var res = ""
        var remain = idx
        do {
            val mod = (remain % alphaBetSize).toInt()
            res = alphaBet[mod].toString() + res
            remain /= alphaBetSize
        } while (remain > 0UL)
        return res
    }

    fun convert(key: String): ULong {
        if (key.isEmpty()) {
            throw InvalidKeySymbolException(key)
        }
        var res = 0UL
        for (pos in key.indices) {
            val alphaBetPos = alphaBet.indexOf(key[key.length - 1 - pos])
            if (alphaBetPos == -1) {
                throw InvalidKeySymbolException(key, key[key.length - 1 - pos])
            }
            val convertKof = alphaBetSize.toDouble().pow(pos.toDouble()).toULong()
            res += alphaBetPos.toULong() * convertKof
        }
        return res
    }
}

data class InvalidKeySymbolException(val key: String, val symbol: Char? = null): RuntimeException()