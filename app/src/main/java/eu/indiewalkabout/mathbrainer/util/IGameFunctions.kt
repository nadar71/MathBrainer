package eu.indiewalkabout.mathbrainer.util


interface IGameFunctions {
    val isGameOver: Boolean
    fun updateProgressBar(progress: Int)
    fun newChallenge()
    fun checkPlayerInput()
    fun checkCountdownExpired()
}
