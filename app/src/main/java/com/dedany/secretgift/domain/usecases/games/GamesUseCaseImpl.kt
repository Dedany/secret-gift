package com.dedany.secretgift.domain.usecases.games

import com.dedany.secretgift.domain.entities.CreateGame
import com.dedany.secretgift.domain.entities.CreatePlayer
import com.dedany.secretgift.domain.entities.Game
import com.dedany.secretgift.domain.entities.LocalGame
import com.dedany.secretgift.domain.entities.Player
import com.dedany.secretgift.domain.entities.Rule
import com.dedany.secretgift.domain.repositories.GamesRepository
import java.util.Date
import javax.inject.Inject

class GamesUseCaseImpl @Inject constructor(
    private val repository: GamesRepository,
) : GamesUseCase {
    override suspend fun getGame(gameCode: String): Game {
        val game = repository.getGame(gameCode)
        return game
    }

    override suspend fun getLocalGame(gameId: Int): LocalGame {
        return repository.getLocalGame(gameId)
    }


    override suspend fun getGamesByUser(): List<Game> {
        val games = repository.getGamesByUser()
        return games
    }

    override suspend fun deleteLocalGame(game: LocalGame) {
        repository.deleteLocalGame(game)
    }

    override suspend fun createLocalGame(game: LocalGame) {
        repository.createLocalGame(game)
    }

    override suspend fun updateLocalGame(game: LocalGame) {
        repository.updateLocalGame(game)
    }

    override suspend fun createGame(gameId: Int): Boolean {
        val localGame = getLocalGame(gameId)
        val pairings = generatePairings(localGame.players, localGame.rules)
        val createGame = convertToCreateGame(localGame, pairings)
        return repository.createGame(createGame)
    }

    override suspend fun updateGame(game: Game) {
        repository.updateGame(game)
    }

    override suspend fun deleteGame(game: Game) {
        repository.deleteGame(game)
    }

    private fun generatePairings(
        players: List<Player>,
        rules: List<Rule>?
    ): List<Pair<Player, Player>> {
        //  Create copia mutable de players
        val availablePlayers = players.toMutableList()
        val pairings = mutableListOf<Pair<Player, Player>>()

        //  Crea un set de parejas no permitidas
        val forbiddenPairs = rules?.map { Pair(it.playerOne, it.playerTwo) }?.toSet() ?: emptySet()

        //  Reordena al azar la lista de jugadores
        availablePlayers.shuffle()

        //  Itera mientras quedan jugadores por emparejar
        for (i in availablePlayers.indices) {
            val player1 = availablePlayers[i]
            var player2: Player? = null

            //  Encuentra pareja para player1 que cumpla con las reglas
            val suitablePartners = availablePlayers.filter {
                it != player1 && !forbiddenPairs.contains(Pair(player1.name, it.name)) && !forbiddenPairs.contains(Pair(it.name, player1.name))
            }

            //  Si no se encuentra pareja válida, retorna lista vacía
            if (suitablePartners.isEmpty()) {

                println("No hay pareja válida para ${player1.name}")
                return emptyList()
            }

            //  Elije pareja al azar de los jugadores válidos
            player2 = suitablePartners.random()

            // Añade emparejamiento a la lista
            pairings.add(Pair(player1, player2))
        }


        return pairings
    }
    private fun convertToCreateGame(localGame: LocalGame, pairings: List<Pair<Player, Player>>): CreateGame {
        val createPlayers = mutableListOf<CreatePlayer>()
        for ((player1, player2) in pairings) {
            createPlayers.add(CreatePlayer(player1.name, player1.email, player2.name))
        }

//        val dateFormat = SimpleDateFormat("dd/MM/yyyy", androidx.compose.ui.text.intl.Locale.getDefault())
//        val gameDateString = localGame.gameDate?.let { dateFormat.format(it) } ?: ""

        return CreateGame(
            name = localGame.name,
            ownerId = localGame.ownerId,
            players = createPlayers,
            maxCost = localGame.maxCost,
            minCost = localGame.minCost,
            gameDate = localGame.gameDate ?: Date(),
            rules = localGame.rules,
            status = "active"
        )
    }

}