import Controllers.FootballAPI
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import Persistence.Serializer
import models.NFLPosition
import models.Player

class FootballAPITest {

    private lateinit var footballAPI: FootballAPI
    private val serializer: Serializer = mock()

    @BeforeEach
    fun setUp() {
        footballAPI = FootballAPI(serializer)
    }

    @Test
    fun testAddPlayer() {
        val player = Player(1234, "John Doe", NFLPosition.RUNNING_BACK, 12000000.00, 1200, false)
        assertTrue(footballAPI.add(player))
        assertEquals(1, footballAPI.numberOfPlayers())
    }

    @Test
    fun testGetPlayerByIndexValidIndex() {
        val player = Player(1234, "John Doe", NFLPosition.RUNNING_BACK, 12000000.00, 1200, false)
        footballAPI.add(player)
        val retrievedPlayer = footballAPI.getPlayerByIndex(0)
        assertNotNull(retrievedPlayer)
        assertEquals(player, retrievedPlayer)
    }

    @Test
    fun testGetPlayerByIndexInvalidIndex() {
        assertNull(footballAPI.getPlayerByIndex(0))
    }

    @Test
    fun testDeletePlayerValidIndex() {
        val player = Player(1234, "John Doe", NFLPosition.RUNNING_BACK, 12000000.00, 1200, false)
        footballAPI.add(player)
        val deletedPlayer = footballAPI.deletePlayer(0)
        assertNotNull(deletedPlayer)
        assertEquals(player, deletedPlayer)
        assertEquals(0, footballAPI.numberOfPlayers())
    }

    @Test
    fun testDeletePlayerInvalidIndex() {
        assertNull(footballAPI.deletePlayer(0))
    }

    @Test
    fun testUpdatePlayerValidIndex() {
        val originalPlayer = Player(1234, "John Doe", NFLPosition.RUNNING_BACK, 12000000.00, 1200, false)
        footballAPI.add(originalPlayer)

        val updatedPlayer = Player(1, "Updated Name", NFLPosition.QUARTERBACK, 12000000.00, 2, false)

        assertTrue(footballAPI.updatePlayer(0, updatedPlayer))

        val retrievedPlayer = footballAPI.getPlayerByIndex(0)
        assertNotNull(retrievedPlayer)
        assertEquals(updatedPlayer, retrievedPlayer)
    }


    @Test
    fun testUpdatePlayerInvalidIndex() {

        val player = Player(1, "John Doe", NFLPosition.RUNNING_BACK, 12000000.00, 1200, false)
        footballAPI.add(player)

        assertFalse(footballAPI.updatePlayer(0, Player(1, "Updated Name", NFLPosition.QUARTERBACK, 12000000.00, 2, false)))
    }


    @Test
    fun testRetirePlayerValidIndex() {
        val player = Player(1, "John Doe", 1)
        footballAPI.add(player)
        assertTrue(footballAPI.retirePlayer(0))
        assertTrue(footballAPI.getPlayerByIndex(0)?.isPlayerRetired ?: false)
    }

    @Test
    fun testRetirePlayerInvalidIndex() {
        assertFalse(footballAPI.retirePlayer(0))
    }

    @Test
    fun testSearchByTitle() {
        val player1 = Player(1, "John Doe", 1)
        val player2 = Player(2, "Jane Doe", 2)
        footballAPI.add(player1)
        footballAPI.add(player2)

        val searchResults = footballAPI.searchByTitle("John")
        assertEquals("0: $player1", searchResults)
    }

    @Test
    fun testLoad() {
        val playersList = listOf(Player(1, "John Doe", 1), Player(2, "Jane Doe", 2))
        whenever(serializer.read()).thenReturn(playersList)
        footballAPI.load()
        assertEquals(playersList.size, footballAPI.numberOfPlayers())
    }

    @Test
    fun testStore() {
        val playersList = listOf(Player(1, "John Doe", 1), Player(2, "Jane Doe", 2))
        footballAPI.add(playersList[0])
        footballAPI.add(playersList[1])

        footballAPI.store()

        Mockito.verify(serializer).write(footballAPI.playersArray)
    }
}
