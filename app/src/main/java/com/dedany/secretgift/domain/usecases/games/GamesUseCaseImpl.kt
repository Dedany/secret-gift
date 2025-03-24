package com.dedany.secretgift.domain.usecases.games

import com.dedany.secretgift.domain.entities.CreateGame
import com.dedany.secretgift.domain.entities.CreatePlayer
import com.dedany.secretgift.domain.entities.Game
import com.dedany.secretgift.domain.entities.GameSummary
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

    override suspend fun getLocalGamesById(id: Int): LocalGame {
        return repository.getLocalGameById(id)
    }


    override suspend fun getOwnedGamesByUser(): List<GameSummary> {
        val games = repository.getOwnedGamesByUser()
        return games
    }

    override suspend fun deleteLocalGame(gameId:Int): Boolean {
        return repository.deleteLocalGame(gameId)
    }

    override suspend fun createLocalGame(game: LocalGame): Long {
        return repository.createLocalGame(game)
    }

    override suspend fun updateLocalGame(game: LocalGame): Int {
        return repository.updateLocalGame(game)
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

    override suspend fun deleteGame(gameId: String, userId: String): Boolean {
       return repository.deleteGame(gameId, userId)
    }

    override suspend fun deleteAllGames(): Boolean{
        return repository.deleteAllGames()
    }

    override suspend fun sendMailToPlayer(gameId: String, playerId: String, playerEmail: String):Boolean {
        return repository.sendMailToPlayer(gameId, playerId, playerEmail)
    }

    override suspend fun getPlayedGamesByUser(): List<GameSummary> {
        val games = repository.getPlayedGamesByUser()
        return games
    }

    override suspend fun getLocalGamesByUser(): List<LocalGame> {
        val games = repository.getLocalGamesByUser()
        return games
    }


    override suspend fun canAssignGift(
        participants: List<Player>,
        restrictions: Map<String, Set<String>>
    ): Boolean {
        val total = participants.size

        for (participant in participants) {
            val possibleReceivers = participants.toMutableSet().apply { remove(participant) }
            restrictions[participant.name]?.let { participantRestrictions ->
                possibleReceivers.removeAll {
                    it.name in participantRestrictions
                }
            }
            if (possibleReceivers.isEmpty()) return false
        }

        return total > 2 && participants.all { participant ->
            participants.any {
                it != participant && !(restrictions[participant.name]?.contains(it.name) ?: false)
            }
        }
    }

    private fun backtrack(
        participants: List<Player>,
        remaining: List<Player>,
        assigned: MutableMap<Player, Player>,
        restrictions: Map<String, Set<String>>
    ): Boolean {
        if (remaining.isEmpty()) return true

        val giver = remaining.first()
        val possibleReceivers = participants.filter {
            it != giver && it !in assigned.values && !(restrictions[giver.name]?.contains(it.name)
                ?: false)
        }

        for (receiver in possibleReceivers) {
            assigned[giver] = receiver
            if (backtrack(participants, remaining.drop(1), assigned, restrictions)) return true
            assigned.remove(giver)
        }

        return false
    }

    private fun assignGifts(
        participants: List<Player>,
        restrictions: Map<String, Set<String>>
    ): Map<Player, Player> {
        val assignments = mutableMapOf<Player, Player>()
        backtrack(participants, participants.shuffled(), assignments, restrictions)
        return assignments
    }

    private fun generatePairings(
        players: List<Player>,
        rules: List<Rule> = emptyList()
    ): List<Pair<Player, Player>> {
        val restrictions: MutableMap<String, Set<String>> = mutableMapOf()
        rules.forEach { rule ->
            restrictions[rule.playerOne] =
                rules.filter { it.playerOne == rule.playerOne }.map { it.playerTwo }.toSet()
        }

        val assignments = assignGifts(players, restrictions)
        return assignments.map { Pair(it.key, it.value) }

//        //  Crea copia mutable de players
//        val availablePlayers = players.toMutableList()
//        val pairings = mutableListOf<Pair<Player, Player>>()
//        val alreadySelectedPlayers = mutableListOf<String>()
//
//        //  Crea un set de parejas no permitidas
//        val forbiddenPairs = rules?.map { Pair(it.playerOne, it.playerTwo) }?.toSet() ?: emptySet()
//
//        //  Reordena al azar la lista de jugadores
//        availablePlayers.shuffle()
//
//        //  Itera mientras quedan jugadores por emparejar
//        for (i in availablePlayers.indices) {
//            val player1 = availablePlayers[i]
//            var player2: Player? = null
//
//            //  Encuentra pareja para player1 que cumpla con las reglas
//            val suitablePartners = availablePlayers.filter {
//                it != player1 &&
//                        !forbiddenPairs.contains(
//                            Pair(
//                                player1.name,
//                                it.name
//                            )
//                        ) && !forbiddenPairs.contains(Pair(it.name, player1.name)) &&
//                        pairings.map { it.second }.contains(it).not()
//            }
//
//            //  Si no se encuentra pareja válida, retorna lista vacía
//            if (suitablePartners.isEmpty()) {
//
//                println("No hay pareja válida para ${player1.name}")
//                return emptyList()
//            }
//
//            //  Elije pareja al azar de los jugadores válidos
//            player2 = suitablePartners.random()
//
//            // Añade emparejamiento a la lista
//            pairings.add(Pair(player1, player2))
//            alreadySelectedPlayers.add(player2.name)
//            if (availablePlayers.size % 2 != 0) {
//                alreadySelectedPlayers.add(player1.name)
//            }
//
//        }
//
//
//        return pairings
    }

    private fun convertToCreateGame(
        localGame: LocalGame,
        pairings: List<Pair<Player, Player>>
    ): CreateGame {
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