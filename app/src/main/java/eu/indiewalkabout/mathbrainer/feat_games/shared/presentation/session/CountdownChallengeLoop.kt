package eu.indiewalkabout.mathbrainer.feat_games.shared.presentation.session

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CountdownChallengeLoop(
    private val scope: CoroutineScope,
    private val countdownStepMs: Long,
    private val nextChallengeDelayMs: Long
) {
    private var countdownJob: Job? = null
    private var delayedChallengeJob: Job? = null

    fun cancelCountdown() {
        countdownJob?.cancel()
    }

    fun cancelAll() {
        countdownJob?.cancel()
        delayedChallengeJob?.cancel()
    }

    fun startCountdown(
        durationMs: Long,
        onTick: (Long) -> Unit,
        onExpired: () -> Unit
    ) {
        cancelCountdown()
        countdownJob = scope.launch {
            var remaining = durationMs
            while (remaining > 0) {
                delay(countdownStepMs)
                remaining = (remaining - countdownStepMs).coerceAtLeast(0L)
                onTick(remaining)
            }
            onExpired()
        }
    }

    fun scheduleNextChallenge(
        shouldLaunch: () -> Boolean,
        onLaunch: suspend () -> Unit
    ) {
        delayedChallengeJob?.cancel()
        delayedChallengeJob = scope.launch {
            delay(nextChallengeDelayMs)
            if (shouldLaunch()) {
                onLaunch()
            }
        }
    }
}
