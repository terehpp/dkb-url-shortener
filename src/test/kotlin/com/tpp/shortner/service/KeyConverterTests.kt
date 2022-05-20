package com.tpp.shortner.service

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import java.util.stream.Stream

@SpringBootTest
@TestPropertySource(locations = ["classpath:application-empty-db.properties"])
class KeyConverterTests(@Autowired val keyConverter: KeyConverter) {
    companion object {
        @JvmStatic
        fun commonCases(): Stream<Arguments?>? {
            return Stream.of(
                Arguments.of(0L, "A"),
                Arguments.of(1L, "B"),
                Arguments.of(2L, "C"),
                Arguments.of(32L, "a"),
                Arguments.of(1234L, "VQ"),
                Arguments.of(1000000L, "FHPW"),
                Arguments.of(-1L, "kp_LafePg]X"),
                Arguments.of(-2L, "kp_LafePg]W"),
            )
        }

        @JvmStatic
        fun invalidKeyCases(): Stream<Arguments?>? {
            return Stream.of(
                Arguments.of(""),
                Arguments.of("±±±±"),
                Arguments.of("    "),
                Arguments.of("\u1212"),
            )
        }
    }

    @ParameterizedTest
    @MethodSource("commonCases")
    fun longToKey(id: Long, key: String) {
        Assertions.assertEquals(key, keyConverter.convert(id.toULong()))
    }

    @ParameterizedTest
    @MethodSource("commonCases")
    fun keyToLong(id: Long, key: String) {
        Assertions.assertEquals(id, keyConverter.convert(key).toLong())
    }

    @ParameterizedTest
    @MethodSource("invalidKeyCases")
    fun invalidKeyTest(invalidKey: String) {
        Assertions.assertThrows(InvalidKeySymbolException::class.java) { keyConverter.convert(invalidKey) }
    }
}