package com.example.presentation.util

import cmpcourseapp.feature.chat.presentation.generated.resources.Res
import cmpcourseapp.feature.chat.presentation.generated.resources.today
import cmpcourseapp.feature.chat.presentation.generated.resources.today_x
import cmpcourseapp.feature.chat.presentation.generated.resources.yesterday
import cmpcourseapp.feature.chat.presentation.generated.resources.yesterday_x
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.char
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Instant

object DateUtil {
    fun formatMessageTime(
        instant: Instant,
        clock: Clock = Clock.System
    ): UiText {
        val timeZone = TimeZone.currentSystemDefault()
        val messageDateTime = instant.toLocalDateTime(timeZone)
        val nowDateTime = clock.now().toLocalDateTime(timeZone).date

        val yesterday = nowDateTime.minus(1, DateTimeUnit.DAY)
        val formattedTime = messageDateTime.format(
            LocalDateTime.Format {
                amPmHour()
                char(':')
                minute()
                amPmMarker("am", "pm")
            }
        )
        val formattedDate = messageDateTime.format(
            LocalDateTime.Format {
                day()
                char('/')
                monthNumber()
                char('/')
                year()
                chars(", ")
                chars(formattedTime)
            }
        )
        return when (messageDateTime.date) {
            nowDateTime -> UiText.MyStringResource(Res.string.today_x, arrayOf(formattedTime))
            yesterday -> UiText.MyStringResource(Res.string.yesterday_x, arrayOf(formattedTime))
            else -> UiText.DynamicString(formattedDate)
        }
    }

    fun formatDateSeparator(date: LocalDate, clock: Clock = Clock.System): UiText {
        val timeZone = TimeZone.currentSystemDefault()
        val nowDateTime = clock.now().toLocalDateTime(timeZone).date

        val yesterday = nowDateTime.minus(1, DateTimeUnit.DAY)
        return when (date) {
            nowDateTime -> UiText.MyStringResource(Res.string.today)
            yesterday -> UiText.MyStringResource(Res.string.yesterday)
            else -> UiText.DynamicString(
                date.format(
                    LocalDate.Format {
                        day()
                        char('/')
                        monthNumber()
                        char('/')
                        year()
                    }
                )
            )
        }
    }
}
