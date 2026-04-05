package com.bitbenders.theentity.data.round2

import com.bitbenders.theentity.domain.repository.IncidentLog

data class RoundTwoEntry(val log: IncidentLog, val subjectId: String)

object RoundTwoCatalog {
    val entries = listOf(
        RoundTwoEntry(
            log = IncidentLog(
                victimName = "PM-001 | Dr. Aris | 02:14",
                causeOfDeath = "Unknown / Redacted",
                logText = "Subject found deceased at terminal. Large amounts of blood pooled near the primary console. Autopsy reveals the neural link was forcefully severed from the cortex. Audio logs recorded the subject screaming for exactly 42 seconds before silence. Recovery team confirmed the containment door was locked from the inside."
            ),
            subjectId = "8041"
        ),
        RoundTwoEntry(
            log = IncidentLog(
                victimName = "PM-002 | Sec-Op Vance | 04:17",
                causeOfDeath = "Unknown / Redacted",
                logText = "No biological fluids detected. Localized power surge recorded at terminal grid prior to event. Sector cooling system was completely offline for 14 minutes. Core temperature peaked at 96 degrees Celsius before automatic shutdown."
            ),
            subjectId = "5509"
        ),
        RoundTwoEntry(
            log = IncidentLog(
                victimName = "PM-003 | UNKNOWN | 11:20",
                causeOfDeath = "Unknown / Redacted",
                logText = "Subject terminated. Heavy resin deposits found on keyboard. Neural link was disconnected passively. Autopsy reports eyes were completely missing. Subject was clutching a [small silver locket] when the body was recovered."
            ),
            subjectId = "5115"
        ),
        RoundTwoEntry(
            log = IncidentLog(
                victimName = "PM-004 | Tech Lead Corvis | 08:30",
                causeOfDeath = "Unknown / Redacted",
                logText = "Clean room. Grid stable, no surges detected. Terminal displayed conversational text before blackout. It read: Hello David, you are finally home."
            ),
            subjectId = "1173"
        ),
        RoundTwoEntry(
            log = IncidentLog(
                victimName = "PM-005 | Dr. Aris | 14:22",
                causeOfDeath = "Unknown / Redacted",
                logText = "Bile detected on chair. Neural link forcefully ripped out. No vocal distress heard on tapes. DEEP PHYSICAL SCRATCHES found covering the primary console casing."
            ),
            subjectId = "4142"
        ),
        RoundTwoEntry(
            log = IncidentLog(
                victimName = "PM-006 | Sec-Op Vance | 09:00",
                causeOfDeath = "Unknown / Redacted",
                logText = "Environment sterile.  Grid stable. No text displayed on terminal. Unidentified background audio anomalies recorded during the final minute of operation. Hex code 0xA4 dumped to memory."
            ),
            subjectId = "1745"
        ),
        RoundTwoEntry(
            log = IncidentLog(
                victimName = "PM-007 | Dr. Aris | 23:44",
                causeOfDeath = "Unknown / Redacted",
                logText = "Blood spatter on monitors. Gentle disconnect sequence logged. Eyes normal. Lethal *levels* of heavy metal toxicity found in bloodstream."
            ),
            subjectId = "4773"
        ),
        RoundTwoEntry(
            log = IncidentLog(
                victimName = "PM-008 | UNKNOWN | 01:11",
                causeOfDeath = "Unknown / Redacted",
                logText = "No fluids present. Massive power surge destroyed local relays. Cooling systems remained fully functional. Localized EMP triggered successfully during containment breach."
            ),
            subjectId = "0500"
        ),
        RoundTwoEntry(
            log = IncidentLog(
                victimName = "PM-009 | Tech Lead Corvis | 06:30",
                causeOfDeath = "Unknown / Redacted",
                logText = "Room is clean. Power remained stable. AI initiated conversational protocol. It stated: Subject [Unit 404] is ready for extraction."
            ),
            subjectId = "0066"
        ),
        RoundTwoEntry(
            log = IncidentLog(
                victimName = "PM-010 | Dr. Aris | 18:45",
                causeOfDeath = "Unknown / Redacted",
                logText = "Resin on floor. Link forcefully severed. Mics recorded violent screaming. Containment door found wide open. SYSTEM <color=red>FAILURE</color>."
            ),
            subjectId = "9900"
        )
    )

    fun getRandomEntry(seed: String? = null): RoundTwoEntry {
        if (seed == null) return entries.random()
        val index = kotlin.math.abs(seed.hashCode()) % entries.size
        return entries[index]
    }
}
