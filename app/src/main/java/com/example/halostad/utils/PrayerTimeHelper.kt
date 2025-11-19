package com.example.halostad.utils

// Import dari Adhan2
import com.batoulapps.adhan2.CalculationMethod
import com.batoulapps.adhan2.Coordinates
import com.batoulapps.adhan2.Madhab
import com.batoulapps.adhan2.PrayerTimes
import com.batoulapps.adhan2.data.DateComponents

// PENTING: Import Clock dan Instant dari 'kotlin.time' (bukan kotlinx.datetime lagi)
import kotlin.time.Clock
import kotlin.time.Instant

// Import TimeZone tetap dari java.util untuk formatter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.time.ExperimentalTime

object PrayerTimeHelper {

    data class JadwalSholat(
        val subuh: String,
        val dzuhur: String,
        val ashar: String,
        val maghrib: String,
        val isya: String,
        val lokasi: String = "Lokasi Kamu"
    )

    // Fungsi ekstensi: Mengubah Instant (kotlin.time) ke Java Date (untuk formatting)
    @OptIn(ExperimentalTime::class)
    private fun Instant.asDate() = Date(toEpochMilliseconds())

    @OptIn(ExperimentalTime::class)
    fun getPrayerTimes(latitude: Double, longitude: Double): JadwalSholat {
        // 1. Koordinat
        val coordinates = Coordinates(latitude, longitude)

        // 2. Tanggal (Menggunakan Clock.System.now() dari kotlin.time)
        val now = Clock.System.now()
        val dateComponents = DateComponents.from(now)

        // 3. Parameter (Singapore / MABIMS + Syafi'i)
        val params = CalculationMethod.SINGAPORE.parameters
            .copy(madhab = Madhab.SHAFI)

        // 4. Hitung Waktu Sholat
        val prayerTimes = PrayerTimes(coordinates, dateComponents, params)

        // 5. Format Waktu (HH:mm)
        val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
        formatter.timeZone = TimeZone.getDefault()

        return JadwalSholat(
            subuh = formatter.format(prayerTimes.fajr.asDate()),
            dzuhur = formatter.format(prayerTimes.dhuhr.asDate()),
            ashar = formatter.format(prayerTimes.asr.asDate()),
            maghrib = formatter.format(prayerTimes.maghrib.asDate()),
            isya = formatter.format(prayerTimes.isha.asDate())
        )
    }
}