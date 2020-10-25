package com.falcon.turingx.core.date

import android.content.Context
import com.falcon.turingx.core.R
import java.util.*
import kotlin.math.abs
import com.falcon.turingx.core.date.TXDate.TimeUnitInMilliseconds.*
import kotlin.math.max

/**
 * A Date utils.
 *
 * */
object TXDate {

    /**
     * Returns elapsed time from now to [timestamp].
     *
     * EX:
     * 5 segundos atrás.
     * 3 minutos atrás.
     * 9 horas atrás.
     * 2 dias atrás.
     * 1 semana atrás.
     * 1 mês atrás.
     * 1 ano atrás.
     * */
    fun getElapsedTime(timestamp: Long, context: Context): String {

        /**
         * Return the max unit time possible by current time minus [timestamp].
         * */
        fun getTimeUnit(timestamp: Long): TimeUnitInMilliseconds {
            return when (abs(Date().time.minus(timestamp))) {
                in 0 until MINUTE.ms -> SECOND
                in MINUTE.ms until HOUR.ms -> MINUTE
                in HOUR.ms until DAY.ms -> HOUR
                in DAY.ms until WEEK.ms -> DAY
                in WEEK.ms until MONTH.ms -> WEEK
                in MONTH.ms until YEAR.ms -> MONTH
                else -> YEAR
            }
        }
        // get the max time unit.
        val timeUnit = getTimeUnit(timestamp)
        // with the discover time unit, get max amount of time with the time unit.
        val timeAmount = max(getElapsedInTimeUnit(timeUnit, timestamp), 0)
        // with time unit get the desired string plural to time.
        val timeId = when (timeUnit) {
            SECOND -> R.plurals.second
            MINUTE -> R.plurals.minute
            HOUR -> R.plurals.hour
            DAY -> R.plurals.day
            WEEK -> R.plurals.week
            MONTH -> R.plurals.month
            YEAR -> R.plurals.year
        }
        // return formatted string.
        return "$timeAmount ${context.resources.getQuantityString(timeId, timeAmount)} atrás"
    }

    /**
     * Return the amount of time in the current timeUnit
     *
     * Ex:
     *
     * 86400000L represents 1 day, if [timestamp] its equal to 2 x 86400000L, will return 2.
     * 60000L represents 1 minute, if [timestamp] its equal to 42 x 60000L, will return 42.
     *
     * */
    private fun getElapsedInTimeUnit(timeUnit: TimeUnitInMilliseconds, timestamp: Long): Int {
        return ((Date().time.minus(timestamp)) / timeUnit.ms).toInt()
    }

    /** The amount of milliseconds in each TimeUnit. */
    enum class TimeUnitInMilliseconds(val ms: Long) {
        SECOND(1000L),
        MINUTE(60000L),
        HOUR(3600000L),
        DAY(86400000L),
        WEEK(604800000L),
        MONTH(2629743000L),
        YEAR(31556926000L)
    }

}



