package com.lyd.absolverdatabase.utils.logUtils.logWrite

class LogDefaultWriter(
    private val formatStrategy :BaseFormatStrategy = LogWriteDefaultFormatStrategy(),
    private val diskStrategy :BaseLogDiskStrategy
) :BaseLogWriter() {
    override fun getLogcatFormatStrategy(): BaseFormatStrategy {
        return formatStrategy
    }

    override fun getLogDiskStrategy(): BaseLogDiskStrategy {
        return diskStrategy
    }
}