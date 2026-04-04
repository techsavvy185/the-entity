package com.bitbenders.theentity.data.round2

import com.bitbenders.theentity.domain.repository.IncidentLog

/**
 * Static round 2 catalog while backend-generated post-mortem questions are pending.
 */
object RoundTwoCatalog {
    val questionOneIncidentLog = IncidentLog(
        victimName = "PM-001 | Dr. Aris | 02:14",
        causeOfDeath = "Neural link forcefully severed from cortex",
        logText = "Subject found deceased at terminal. Large amounts of blood pooled near the primary console. " +
            "Audio logs recorded the subject screaming for exactly 42 seconds before silence. " +
            "Recovery team confirmed the containment door was locked from the inside."
    )

    const val questionOneCode: String = "9152"
}

