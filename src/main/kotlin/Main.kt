import controllers.FootballAPI
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
         > |   5) Archive/Retire a Player        |
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
            5 -> archivePlayer()
            6 -> searchPlayers()
            20  -> save()
            21  -> load()
            0  -> exitApp()
            else -> println("Invalid option entered: ${option}")
        }
    } while (true)
}

fun addPlayer(){
    //logger.info { "addPlayer() function invoked" }
    val id = readNextLine("Enter player ID Number: ")
    val name = readNextLine("Enter players Full Name:")
    val position = readValidPriority("Enter players position: ")
    val cost = readValidCategory("Enter a category for the Player from ${CategoryUtility.categories}: ")
    val ranking = readNextLine("Enter the players current NFL Rank: ")
    val isRetired = readNextLine("Enter (y/n) if the player is retired: ")

    val isAdded = footballAPI.add(Player(id,name,position,cost,ranking,isRetired, false))

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
    //logger.info { "updatePlayers() function invoked" }
    listPlayers()
    if (footballAPI.numberOfPlayers() > 0) {
        //only ask the user to choose the Player if Players exist
        val indexToUpdate = readNextInt("Enter the index of the Player to update: ")
        if (footballAPI.isValidIndex(indexToUpdate)) {
            val PlayerTitle = readNextLine("Enter a title for the Player: ")
            val PlayerPriority = readValidPriority("Enter a priority (1-low, 2, 3, 4, 5-high): ")
            val PlayerCategory = readValidCategory("Enter a category for the Player from ${CategoryUtility.categories}: ")

            //pass the index of the Player and the new Player details to FootballAPI for updating and check for success.
            if (footballAPI.updatePlayer(indexToUpdate, Player(PlayerTitle, PlayerPriority, PlayerCategory, false))){
                println("Update Successful")
            } else {
                println("Update Failed")
            }
        } else {
            println("There are no Players for this index number")
        }
    }
}

fun deletePlayer(){
    //logger.info { "deletePlayer() function invoked" }
    listPlayers()
    if (footballAPI.numberOfPlayers() > 0) {
        //only ask the user to choose the Player to delete if Players exist
        val indexToDelete = readNextInt("Enter the index of the Player to delete: ")
        //pass the index of the Player to FootballAPI for deleting and check for success.
        val PlayerToDelete = footballAPI.deletePlayer(indexToDelete)
        if (PlayerToDelete != null) {
            println("Delete Successful! Deleted Player: ${PlayerToDelete.PlayerTitle}")
        } else {
            println("Delete NOT Successful")
        }
    }
}

fun archivePlayer() {
    listActivePlayers()
    if (footballAPI.numberOfActivePlayers() > 0) {
        //only ask the user to choose the Player to archive if active Players exist
        val indexToArchive = readNextInt("Enter the index of the Player to archive: ")
        //pass the index of the Player to FootballAPI for archiving and check for success.
        if (footballAPI.archivePlayer(indexToArchive)) {
            println("Archive Successful!")
        } else {
            println("Archive NOT Successful")
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
