package controllers

import models.Player
import models.Award
import persistence.Serializer
import utils.Utilities.isValidListIndex

class FootballAPI(serializerType: Serializer){

    private var serializer: Serializer = serializerType

    private var Players = ArrayList<Player>()

    fun add(Player: Player): Boolean {
        return Players.add(Player)
    }

    fun deletePlayer(indexToDelete: Int): Player? {
        return if (isValidListIndex(indexToDelete, Players)) {
            Players.removeAt(indexToDelete)
        } else null
    }

    fun updatePlayer(indexToUpdate: Int, Player: Player?): Boolean {
        //find the Player object by the index number
        val foundPlayer = findPlayer(indexToUpdate)

        //if the Player exists, use the Player details passed as parameters to update the found Player in the ArrayList.
        if ((foundPlayer != null) && (Player != null)) {
            foundPlayer.name = Player.name
            foundPlayer.ranking = Player.ranking
            foundPlayer.position = Player.position
            return true
        }

        //if the Player was not found, return false, indicating that the update was not successful
        return false
    }

    fun archivePlayer(indexToArchive: Int): Boolean {
        if (isValidIndex(indexToArchive)) {
            val PlayerToArchive = Players[indexToArchive]
            if (!PlayerToArchive.isPlayerRetired) {
                PlayerToArchive.isPlayerRetired = true
                return true
            }
        }
        return false
    }

    fun listAllPlayers(): String =
        if (Players.isEmpty())  "No Players Stored"
        else formatListString(Players)

    fun listActivePlayers(): String =
        if (numberOfActivePlayers() == 0) "No Active Players Stored"
        else formatListString(Players.filter{ Player -> !Player.isPlayerRetired })

    fun listRetiredPlayers(): String =
        if (numberOfRetiredPlayers() == 0) "No Retired Players Stored"
        else formatListString(Players.filter{ Player -> Player.isPlayerRetired })

    fun listPlayersBySelectedPriority(priority: Int): String =
        if (Players.isEmpty()) "No Players stored"
        else {
            val listOfPlayers = formatListString(Players.filter{ Player -> Player.ranking == priority})
            if (listOfPlayers.equals("")) "No Players with Ranking: $priority"
            else "${numberOfPlayersByPriority(priority)} Players with Ranking $priority: $listOfPlayers"
        }

    fun numberOfPlayers(): Int = Players.size
    fun numberOfActivePlayers(): Int = Players.count{Player: Player -> !Player.isPlayerRetired}
    fun numberOfRetiredPlayers(): Int = Players.count{Player: Player -> Player.isPlayerRetired}
    fun numberOfPlayersByPriority(priority: Int): Int = Players.count { p: Player -> p.ranking == priority }

    fun searchByTitle(searchString : String) =
        formatListString(Players.filter { Player -> Player.name.contains(searchString, ignoreCase = true)})

    fun findPlayer(index: Int): Player? {
        return if (isValidListIndex(index, Players)) {
            Players[index]
        } else null
    }

    fun isValidIndex(index: Int) :Boolean{
        return isValidListIndex(index, Players);
    }

    @Throws(Exception::class)
    fun load() {
        Players = serializer.read() as ArrayList<Player>
    }

    @Throws(Exception::class)
    fun store() {
        serializer.write(Players)
    }

    private fun formatListString(PlayersToFormat : List<Player>) : String =
        PlayersToFormat
            .joinToString (separator = "\n") { Player ->
                Players.indexOf(Player).toString() + ": " + Player.toString() }

}