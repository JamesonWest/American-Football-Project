package models

data class Award(var awardName: String, var year: Int, var playerName: String, var position: String, var team: String){
    val mvp2022 = Award(awardName = "Most Valuable Player", year = 2022, playerName = "Patrick Mahomes", position = "Quarterback", team = "Chiefs")
    val dpoy2022 = Award(awardName = "Defensive Player of the Year", year = 2022, playerName = "Nick Bosa", position = "Defensive End", team = "49ers")
    val opoy2022 = Award(awardName = "Offensive Player of the Year", year = 2022, playerName = "Justin Jefferson", position = "Wide Receiver", team = "Vikings")
    val cbpoy2022 = Award(awardName = "Comeback Player of the Year", year = 2022, playerName = "Geno Smith", position = "Quarterback", team = "Seahawks")
    val orpoy2022 = Award(awardName = "Offensive Rookie of the Year", year = 2022, playerName = "Garrett Wilson", position = "Wide Receiver", team = "Jets")
    val drpoy2022 = Award(awardName = "Defensive Rookie of the Year", year = 2022, playerName = "Sauce Gardner", position = "Cornernback", team = "Jets")
}
