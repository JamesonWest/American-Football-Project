package Controllers

import models.Player
import Persistence.Serializer
import utils.Utilities.isValidListIndex

// Controller class for managing football players using a serializer
class FootballAPI(serializerType: Serializer) {

    // Serializer for reading/writing player data
    private var serializer: Serializer = serializerType

    // List of players stored in memory
    var playersArray = ArrayList<Player>()

    // List of players stored in memory as a mutable list
    private val players: MutableList<Player> = mutableListOf()

    // Get a player by its index in the list
    fun getPlayerByIndex(index: Int): Player? {
        return if (isValidIndex(index)) {
            players[index]
        } else {
            null
        }
    }

    // Add a player to the list
    fun add(player: Player): Boolean {
        return playersArray.add(player)
    }

    // Delete a player from the list by its index
    fun deletePlayer(indexToDelete: Int): Player? {
        return if (isValidListIndex(indexToDelete, playersArray)) {
            playersArray.removeAt(indexToDelete)
        } else null
    }

    // Update player details based on its index
    fun updatePlayer(indexToUpdate: Int, player: Player?): Boolean {
        // Find the player object by the index number
        val foundPlayer = findPlayer(indexToUpdate)

        // If the player exists, use the player details passed as parameters to update the found player in the list
        if ((foundPlayer != null) && (player != null)) {
            foundPlayer.name = player.name
            foundPlayer.ranking = player.ranking
            foundPlayer.position = player.position
            return true
        }

        // If the player was not found, return false, indicating that the update was not successful
        return false
    }

    // Mark a player as retired based on its index
    fun retirePlayer(indexToArchive: Int): Boolean {
        if (isValidIndex(indexToArchive)) {
            val playerToArchive = playersArray[indexToArchive]
            if (!playerToArchive.isPlayerRetired) {
                playerToArchive.isPlayerRetired = true
                return true
            }
        }
        return false
    }

    // List all players
    fun listAllPlayers(): String =
        if (playersArray.isEmpty()) "No Players Stored"
        else formatListString(playersArray)

    // List only active players
    fun listActivePlayers(): String =
        if (numberOfActivePlayers() == 0) "No Active Players Stored"
        else formatListString(playersArray.filter { player -> !player.isPlayerRetired })

    // List only retired players
    fun listRetiredPlayers(): String =
        if (numberOfRetiredPlayers() == 0) "No Retired Players Stored"
        else formatListString(playersArray.filter { player -> player.isPlayerRetired })

    // List players based on their ranking
    fun listPlayersBySelectedRank(priority: Int): String =
        if (playersArray.isEmpty()) {
            "No Players stored"
        } else {
            val listOfPlayers = formatListString(playersArray.filter { player -> player.ranking == priority })
            if (listOfPlayers.isEmpty()) {
                "No Players with Ranking: $priority"
            } else {
                "${numberOfPlayersByRank(priority)} Players with Ranking $priority: $listOfPlayers"
            }
        }

    // Get the total number of players
    fun numberOfPlayers(): Int = playersArray.size

    // Get the number of active players
    fun numberOfActivePlayers(): Int = playersArray.count { player -> !player.isPlayerRetired }

    // Get the number of retired players
    private fun numberOfRetiredPlayers(): Int = playersArray.count { player -> player.isPlayerRetired }

    // Get the number of players with a specific ranking
    private fun numberOfPlayersByRank(priority: Int): Int {
        return playersArray.count { player -> player.ranking == priority }
    }

    // Search for players by their name
    fun searchByTitle(searchString: String) =
        formatListString(playersArray.filter { player -> player.name.contains(searchString, ignoreCase = true) })

    // Find a player by its index
    private fun findPlayer(index: Int): Player? {
        return if (isValidListIndex(index, playersArray)) {
            playersArray[index]
        } else null
    }

    // Check if an index is valid for the list
    private fun isValidIndex(index: Int): Boolean {
        return index in 0 until players.size
    }

    // Load player data from the serializer
    @Throws(Exception::class)
    fun load() {
        playersArray = serializer.read() as ArrayList<Player>
    }

    // Store player data using the serializer
    @Throws(Exception::class)
    fun store() {
        serializer.write(playersArray)
    }

    // Format a list of players into a string
    private fun formatListString(playersToFormat: List<Player>): String =
        playersToFormat
            .joinToString(separator = "\n") { player ->
                playersArray.indexOf(player).toString() + ": " + player.toString()
            }
}