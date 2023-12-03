package controllers

import models.Player
import persistence.Serializer
import utils.Utilities.isValidListIndex

class FootballAPI(serializerType: Serializer) {

    private var serializer: Serializer = serializerType

    private var playersArray = ArrayList<Player>()

    private val players: MutableList<Player> = mutableListOf()

    fun getPlayerByIndex(index: Int): Player? {
        return if (isValidIndex(index)) {
            players[index]
        } else {
            null
        }
    }
    fun add(Player: Player): Boolean {
        return playersArray.add(Player)
    }

    fun deletePlayer(indexToDelete: Int): Player? {
        return if (isValidListIndex(indexToDelete, playersArray)) {
            playersArray.removeAt(indexToDelete)
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

    fun retirePlayer(indexToArchive: Int): Boolean {
        if (isValidIndex(indexToArchive)) {
            val PlayerToArchive = playersArray[indexToArchive]
            if (!PlayerToArchive.isPlayerRetired) {
                PlayerToArchive.isPlayerRetired = true
                return true
            }
        }
        return false
    }

    fun listAllPlayers(): String =
        if (playersArray.isEmpty()) "No Players Stored"
        else formatListString(playersArray)

    fun listActivePlayers(): String =
        if (numberOfActivePlayers() == 0) "No Active Players Stored"
        else formatListString(playersArray.filter { Player -> !Player.isPlayerRetired })

    fun listRetiredPlayers(): String =
        if (numberOfRetiredPlayers() == 0) "No Retired Players Stored"
        else formatListString(playersArray.filter { Player -> Player.isPlayerRetired })

    fun listPlayersBySelectedPriority(priority: Int): String =
        if (playersArray.isEmpty()) "No Players stored"
        else {
            val listOfPlayers = formatListString(playersArray.filter { Player -> Player.ranking == priority })
            if (listOfPlayers.equals("")) "No Players with Ranking: $priority"
            else "${numberOfPlayersByPriority(priority)} Players with Ranking $priority: $listOfPlayers"
        }

    fun numberOfPlayers(): Int = playersArray.size
    fun numberOfActivePlayers(): Int = playersArray.count { Player: Player -> !Player.isPlayerRetired }
    fun numberOfRetiredPlayers(): Int = playersArray.count { Player: Player -> Player.isPlayerRetired }
    fun numberOfPlayersByPriority(priority: Int): Int = playersArray.count { p: Player -> p.ranking == priority }

    fun searchByTitle(searchString: String) =
        formatListString(playersArray.filter { Player -> Player.name.contains(searchString, ignoreCase = true) })

    fun findPlayer(index: Int): Player? {
        return if (isValidListIndex(index, playersArray)) {
            playersArray[index]
        } else null
    }

    fun isValidIndex(index: Int): Boolean {
        return index in 0 until players.size
    }

    @Throws(Exception::class)
    fun load() {
        playersArray = serializer.read() as ArrayList<Player>
    }

    @Throws(Exception::class)
    fun store() {
        serializer.write(playersArray)
    }

    private fun formatListString(PlayersToFormat: List<Player>): String =
        PlayersToFormat
            .joinToString(separator = "\n") { Player ->
                playersArray.indexOf(Player).toString() + ": " + Player.toString()
            }
}
