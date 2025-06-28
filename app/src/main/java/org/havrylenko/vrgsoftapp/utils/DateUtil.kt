package org.havrylenko.vrgsoftapp.utils

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateUtil {

    fun formatDisplayDate(dateString: String?): String {
        if (dateString.isNullOrBlank()) return "Unknown Date"

        return try {
            val zonedDateTime = ZonedDateTime.parse(dateString)
            val formatter =
                DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm", Locale.getDefault())
            zonedDateTime.format(formatter)
        } catch (e: Exception) {
            "Invalid Date"
        }
    }
}