package eu.indiewalkabout.mathbrainer.feat_statistics.domain.repository

import eu.indiewalkabout.mathbrainer.feat_statistics.domain.model.GameScores
import eu.indiewalkabout.mathbrainer.feat_statistics.domain.model.GameStatistics

interface MathBrainerRepository {
    //----------------------------------- QUERY ----------------------------------------------------
    suspend fun loadGameScores(): GameScores
    suspend fun loadGameStatistics(): GameStatistics
    //----------------------------------------- INSERT ---------------------------------------------
    suspend fun insertGameScores(gameScores: GameScores)
    suspend fun insertGameStatistics(gameStatistics: GameStatistics)
    //------------------------------------------- DROPS --------------------------------------------
    suspend fun dropTableGameScores()
    suspend fun dropTableGameStatistics()
}