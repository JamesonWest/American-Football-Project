package models

data class Player(
    var id: Int,
    var name: String,
    var position: NFLPosition,
    var cost: Double,
    var ranking: Int,
    var isPlayerRetired: Boolean
)

enum class NFLPosition {
    QUARTERBACK,
    RUNNING_BACK,
    WIDE_RECEIVER,
    TIGHT_END,
    OFFENSIVE_LINE,
    DEFENSIVE_LINE,
    LINEBACKER,
    DEFENSIVE_BACK,
    KICKER,
    PUNTER,
    SPECIAL_TEAMS
}

