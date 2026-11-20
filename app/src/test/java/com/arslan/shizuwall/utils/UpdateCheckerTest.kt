package com.arslan.shizuwall.utils

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UpdateCheckerTest {

    @Test
    fun identicalVersionsAreNotNewer() {
        assertFalse(UpdateChecker.isNewer("v4.7.1", "4.7.1"))
    }

    @Test
    fun patchBumpIsNewer() {
        assertTrue(UpdateChecker.isNewer("v4.7.2", "4.7.1"))
    }

    @Test
    fun olderVersionIsNotNewer() {
        assertFalse(UpdateChecker.isNewer("v4.6.9", "4.7.1"))
    }

    @Test
    fun majorBumpIsNewer() {
        assertTrue(UpdateChecker.isNewer("5.0.0", "4.7.1"))
    }

    @Test
    fun numericComparisonIsNotLexicographic() {
        assertTrue(UpdateChecker.isNewer("v4.10.0", "4.9.9"))
        assertFalse(UpdateChecker.isNewer("v4.9.9", "4.10.0"))
    }

    @Test
    fun numericPrefixesAndMissingSegmentsAreTreatedAsZero() {
        assertFalse(UpdateChecker.isNewer("v", "4.7.1"))
        assertTrue(UpdateChecker.isNewer("4.7.1.1", "4.7.1"))
        assertFalse(UpdateChecker.isNewer("4.7", "4.7.1"))
    }
}