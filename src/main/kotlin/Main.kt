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

private val logger = KotlinLogging.logger {}
//private val FootballAPI = FootballAPI(XMLSerializer(File("Players.xml")))
private val footballAPI = FootballAPI(JSONSerializer(File("Players/Awards.json")))

fun main(args: Array<String>) {
    runMenu()
}

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
         > |   5) Retire/Retire a Player        |
         > |   6) Search Player(by description)  |
         > ---------------------------------------
         > |   20) Save Players                  |
         > |   21) Load Players                  |
         > ---------------------------------------
         > |   0) Exit                           |
         > ---------------------------------------
         > ==>> """.trimMargin(">"))
}

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

fun listAllPlayers() {
    println(footballAPI.listAllPlayers())
}

fun listActivePlayers() {
    println(footballAPI.listActivePlayers())
}

fun listRetiredPlayers() {
    println(footballAPI.listRetiredPlayers())
}

fun updatePlayer() {
    listPlayers()

    if (footballAPI.numberOfPlayers() > 0) {
        // Only ask the user to choose the player if players exist
        val indexToUpdate = readNextInt("Enter the index of the Player to update: ")

        val existingPlayer = footballAPI.getPlayerByIndex(indexToUpdate)

        if (existingPlayer != null) {
            println("Current Player Details:")
            println("Title: ${existingPlayer.title}")
            println("Priority: ${existingPlayer.priority}")
            println("Category: ${existingPlayer.category}")

            // Ask the user for updated details
            val newTitle = readNextLine("Enter a new title for the Player: ")
            val newPriority = readValidPriority("Enter a new priority (1-low, 2, 3, 4, 5-high): ")
            val newCategory = readValidCategory("Enter a new category for the Player from ${CategoryUtility.categories}: ")

            // Create a new Player instance with updated details
            val updatedPlayer = existingPlayer.copy(
                title = newTitle,
                priority = newPriority,
                category = newCategory
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

fun deletePlayer(){
    //logger.info { "deletePlayer() function invoked" }
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

fun retirePlayer() {
    listActivePlayers()
    if (footballAPI.numberOfActivePlayers() > 0) {
        //only ask the user to choose the Player to Retire if active Players exist
        val indexToRetire = readNextInt("Enter the index of the Player to Retire: ")
        //pass the index of the Player to FootballAPI for archiving and check for success.
        if (footballAPI.retirePlayer(indexToRetire)) {
            println("Player has been Retired Successfully!")
        } else {
            println("Player has NOT been Retired Successfully")
        }
    }
}

fun searchPlayers() {
    val searchTitle = readNextLine("Enter the description to search by: ")
    val searchResults = footballAPI.searchByTitle(searchTitle)
    if (searchResults.isEmpty()) {
        println("No Players found")
    } else {
        println(searchResults)
    }
}
fun save() {
    try {
        footballAPI.store()
    } catch (e: Exception) {
        System.err.println("Error writing to file: $e")
    }
}

fun load() {
    try {
        footballAPI.load()
    } catch (e: Exception) {
        System.err.println("Error reading from file: $e")
    }
}

fun exitApp(){
    logger.info { "exitApp() function invoked" }
    exit(0)
}
