package com.example.ui.util

import java.text.NumberFormat
import java.util.Locale

/** Currency and labels that stay correct even if source files are compiled without UTF-8. */
object Formatters {
    private val rupee = "\u20B9"

    fun money(amount: Double, decimals: Int = 2): String {
        val nf = NumberFormat.getNumberInstance(Locale.US).apply {
            minimumFractionDigits = decimals
            maximumFractionDigits = decimals
        }
        return rupee + nf.format(amount)
    }

    fun moneyWhole(amount: Double): String = money(amount, 0)
}
