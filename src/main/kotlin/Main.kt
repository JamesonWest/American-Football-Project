import controllers.FootballAPI
import models.NFLPosition
import models.Player
import mu.KotlinLogging
import persistence.JSONSerializer
import utils.CategoryUtility
import utils.ScannerInput
import utils.ScannerInput.readNextInt
import utils.ScannerInput.readNextLine
import utils.ValidateInput.readValidCategory
import utils.ValidateInput.readValidPriority
import java.io.File
import java.lang.System.exit

// Initialize logger using KotlinLogging library
private val logger = KotlinLogging.logger {}
// Create an instance of FootballAPI with a JSON serializer and a file location
private val footballAPI = FootballAPI(JSONSerializer(File("Players/Awards.json")))

// Entry point of the program
fun main(args: Array<String>) {
    runMenu()
}

// Display the main menu and return the selected option
fun mainMenu() : Int {
    return ScannerInput.readNextInt(""" 
         > ---------------------------------------
         > |          PLAYERS & AWARDS           |
         > ---------------------------------------
         > | Player MENU                         |
         > |   1) Add a Player                   |
         > |   2) List Players                   |
         > |   3) Update a Player                |
         > |   4) Delete a Player                |
         > |   5) Retire/Retire a Player         |
         > |   6) Search Player(by description) |
         > ---------------------------------------
         > |   20) Save Players                  |
         > |   21) Load Players                  |
         > ---------------------------------------
         > |   0) Exit                           |
         > ---------------------------------------
         > ==>> """.trimMargin(">"))
}

// Run the main menu in a loop until the user chooses to exit
fun runMenu() {
    do {
        val option = mainMenu()
        when (option) {
            1  -> addPlayer()
            2  -> listPlayers()
            3  -> updatePlayer()
            4  -> deletePlayer()
            5 -> retirePlayer()
            6 -> searchPlayers()
            20  -> save()
            21  -> load()
            0  -> exitApp()
            else -> println("Invalid option entered: ${option}")
        }
    } while (true)
}

// Read a valid NFL player position from the user
fun readValidPosition(prompt: String): NFLPosition {
    while (true) {
        try {
            val input = readNextLine(prompt)
            val position = NFLPosition.valueOf(input.toUpperCase())
            return position
        } catch (e: IllegalArgumentException) {
            println("Invalid position. Please enter a valid NFL player position.")
        }
    }
}

// Add a new player to the FootballAPI
fun addPlayer(){
    val id = readNextInt("Enter player ID Number: ")
    val name = readNextLine("Enter player's Full Name:")
    val position = readValidPosition("Enter player's position: ")
    val cost = readNextLine("Enter the cost for the Player: ").toDoubleOrNull() ?: 0.0
    val ranking = readNextInt("Enter the player's current NFL Rank: ")
    val isRetired = readNextLine("Enter (y/n) if the player is retired: ") == "y"

    val isAdded = footballAPI.add(Player(id, name, position, cost, ranking, isRetired))
    if (isAdded) {
        println("Player Added Successfully")
    } else {
        println("Adding a Player Failed")
    }
}

// Display a menu for listing players based on user choice
fun listPlayers(){
    if (footballAPI.numberOfPlayers() > 0) {
        val option = readNextInt(
            """
                  > --------------------------------|
                  > |   1) View ALL Players         |
                  > |   2) View ACTIVE Players      |
                  > |   3) View RETIRED Players     |
                  > --------------------------------|
         > ==>> """.trimMargin(">"))

        when (option) {
            1 -> listAllPlayers();
            2 -> listActivePlayers();
            3 -> listRetiredPlayers();
            else -> println("Invalid option entered: " + option);
        }
    } else {
        println("Option Invalid - No Players stored");
    }
}

// List all players
fun listAllPlayers() {
    println(footballAPI.listAllPlayers())
}

// List only active players
fun listActivePlayers() {
    println(footballAPI.listActivePlayers())
}

// List only retired players
fun listRetiredPlayers() {
    println(footballAPI.listRetiredPlayers())
}

// Update an existing player's details
fun updatePlayer() {
    listPlayers()

    if (footballAPI.numberOfPlayers() > 0) {
        // Only ask the user to choose the player if players exist
        val indexToUpdate = readNextInt("Enter the index of the Player to update: ")

        val existingPlayer = footballAPI.getPlayerByIndex(indexToUpdate)

        if (existingPlayer != null) {
            println("Current Player Details:")
            println("Name: ${existingPlayer.name}")
            println("Position: ${existingPlayer.position}")
            println("Ranking: ${existingPlayer.ranking}")

            // Ask the user for updated details
            val newTitle = readNextLine("Enter a new name for the Player: ")
            val newPosition = readValidPosition("Enter player's position: ")
            val newRanking = readNextInt("Enter a new Rank for the Player: ")

            // Create a new Player instance with updated details
            val updatedPlayer = existingPlayer.copy(
                name = newTitle,
                position = newPosition,
                ranking = newRanking,
                cost = existingPlayer.cost,
                isPlayerRetired = existingPlayer.isPlayerRetired
            )

            // Pass the index of the Player and the new Player details to FootballAPI for updating and check for success.
            if (footballAPI.updatePlayer(indexToUpdate, updatedPlayer)) {
                println("Update Successful")
            } else {
                println("Update Failed")
            }
        } else {
            println("Invalid index. There are no Players for this index number.")
        }
    }
}

// Delete an existing player
fun deletePlayer(){
    listPlayers()
    if (footballAPI.numberOfPlayers() > 0) {
        val indexToDelete = readNextInt("Enter the index of the Player to delete: ")

        if (footballAPI.deletePlayer(indexToDelete) != null) {
            println("Delete Successful!")
        } else {
            println("Delete NOT Successful")
        }
    }
}

// Retire an active player
fun retirePlayer() {
    listActivePlayers()
    if (footballAPI.numberOfActivePlayers() > 0) {
        // Only ask the user to choose the Player to Retire if active Players exist
        val indexToRetire = readNextInt("Enter the index of the Player to Retire: ")

        // Pass the index of the Player to FootballAPI for retiring and check for success.
        if (footballAPI.retirePlayer(indexToRetire)) {
            println("Player has been Retired Successfully!")
        } else {
            println("Player has NOT been Retired Successfully")
        }
    }
}

// Search for players based on a description
fun searchPlayers() {
    val searchTitle = readNextLine("Enter the description to search by: ")
    val searchResults = footballAPI.searchByTitle(searchTitle)
    if (searchResults.isEmpty()) {
        println("No Players found")
    } else {
        println(searchResults)
    }
}

// Save the current state of players to a file
fun save() {
    try {
        footballAPI.store()
    } catch (e: Exception) {
        System.err.println("Error writing to file: $e")
    }
}

// Load the players' data from a file
fun load() {
    try {
        footballAPI.load()
    } catch (e: Exception) {
        System.err.println("Error reading from file: $e")
    }
}

// Exit the application
fun exitApp(){
    logger.info { "exitApp() function invoked" }
    exit(0)
}
