package eu.indiewalkabout.mathbrainer.feat_games.feat_enigma.domain.use_cases

import eu.indiewalkabout.mathbrainer.feat_games.feat_enigma.domain.model.EnigmaChallenge
import eu.indiewalkabout.mathbrainer.feat_games.feat_enigma.domain.model.EnigmaConfig
import eu.indiewalkabout.mathbrainer.feat_games.feat_enigma.domain.model.EnigmaEquation
import javax.inject.Inject
import kotlin.math.max
import kotlin.random.Random

class GenerateEnigmaChallengeUseCase @Inject constructor() {

    operator fun invoke(config: EnigmaConfig): EnigmaChallenge {
        val level = max(1, config.level)
        val symbolCount = (2 + (level - 1) / 2).coerceAtMost(MAX_SYMBOLS)
        val symbols = SYMBOL_POOL.take(symbolCount)
        val random = Random(level + symbolCount)

        val valueRangeMin = config.minValue
        val valueRangeMax = max(config.maxValue, config.maxValue + level * 3)
        val values = mutableMapOf<String, Int>()
        val equations = mutableListOf<EnigmaEquation>()

        val first = symbols[0]
        val second = symbols[1]
        val firstValue = random.nextInt(valueRangeMin + 1, valueRangeMax + 1)
        val secondValue = random.nextInt(valueRangeMin, firstValue)

        values[first] = firstValue
        values[second] = secondValue

        equations += EnigmaEquation("$first + $second", firstValue + secondValue)
        equations += EnigmaEquation("$first – $second", firstValue - secondValue)

        val availableOperators = buildOperatorsForLevel(level)

        symbols.drop(2).forEach { symbol ->
            val symbolValue = random.nextInt(valueRangeMin, valueRangeMax + 1)
            values[symbol] = symbolValue
            val knownSymbol = values.keys.random(random)
            val knownValue = values.getValue(knownSymbol)
            equations += buildEquation(
                newSymbol = symbol,
                newValue = symbolValue,
                knownSymbol = knownSymbol,
                knownValue = knownValue,
                operators = availableOperators,
                random = random
            )
        }

        val expressionSymbols = symbols.shuffled(random).take(expressionSymbolCount(level, symbolCount))
        val finalExpression = buildFinalExpression(expressionSymbols, values, availableOperators, random)

        return EnigmaChallenge(
            equations = equations,
            finalExpression = finalExpression.expression,
            answer = finalExpression.answer,
            level = level
        )
    }

    private fun buildEquation(
        newSymbol: String,
        newValue: Int,
        knownSymbol: String,
        knownValue: Int,
        operators: List<Operator>,
        random: Random
    ): EnigmaEquation {
        val shuffledOps = operators.shuffled(random)
        for (op in shuffledOps) {
            when (op) {
                Operator.PLUS -> return EnigmaEquation("$newSymbol + $knownSymbol", newValue + knownValue)
                Operator.MINUS -> {
                    return if (newValue >= knownValue) {
                        EnigmaEquation("$newSymbol - $knownSymbol", newValue - knownValue)
                    } else {
                        EnigmaEquation("$knownSymbol - $newSymbol", knownValue - newValue)
                    }
                }
                Operator.MULTIPLY -> return EnigmaEquation("$newSymbol ${op.symbol} $knownSymbol", newValue * knownValue)
                Operator.DIVIDE -> {
                    if (knownValue != 0 && newValue % knownValue == 0) {
                        return EnigmaEquation("$newSymbol ${op.symbol} $knownSymbol", newValue / knownValue)
                    }
                    if (newValue != 0 && knownValue % newValue == 0) {
                        return EnigmaEquation("$knownSymbol ${op.symbol} $newSymbol", knownValue / newValue)
                    }
                }
            }
        }

        return EnigmaEquation("$newSymbol + $knownSymbol", newValue + knownValue)
    }

    private fun buildFinalExpression(
        symbols: List<String>,
        values: Map<String, Int>,
        operators: List<Operator>,
        random: Random
    ): FinalExpressionResult {
        val multiTerm = symbols.size > 2
        val expressionOperators = if (multiTerm) {
            listOf(Operator.PLUS, Operator.MINUS)
        } else {
            operators
        }

        var expression = symbols.first()
        var result = values.getValue(symbols.first())

        symbols.drop(1).forEach { symbol ->
            val value = values.getValue(symbol)
            val operator = expressionOperators.random(random)
            val resolvedOperator = resolveSafeOperator(operator, result, value)
            expression += " ${resolvedOperator.symbol} $symbol"
            result = resolvedOperator.apply(result, value)
        }

        return FinalExpressionResult(expression = "$expression = ?", answer = result)
    }

    private fun resolveSafeOperator(operator: Operator, current: Int, next: Int): Operator {
        return when (operator) {
            Operator.MINUS -> if (current - next < 0) Operator.PLUS else operator
            Operator.DIVIDE -> if (next != 0 && current % next == 0) operator else Operator.PLUS
            else -> operator
        }
    }

    private fun buildOperatorsForLevel(level: Int): List<Operator> {
        return when {
            level < 3 -> listOf(Operator.PLUS, Operator.MINUS)
            level < 5 -> listOf(Operator.PLUS, Operator.MINUS, Operator.MULTIPLY)
            else -> listOf(Operator.PLUS, Operator.MINUS, Operator.MULTIPLY, Operator.DIVIDE)
        }
    }

    private fun expressionSymbolCount(level: Int, symbolCount: Int): Int {
        val desired = when {
            level < 3 -> 2
            level < 5 -> 3
            else -> 4
        }
        return desired.coerceAtMost(symbolCount)
    }

    private data class FinalExpressionResult(
        val expression: String,
        val answer: Int
    )

    private enum class Operator(val symbol: String, val apply: (Int, Int) -> Int) {
        PLUS("+", { a, b -> a + b }),
        MINUS("–", { a, b -> a - b }),
        MULTIPLY("×", { a, b -> a * b }),
        DIVIDE(":", { a, b -> a / b })
    }

    private companion object {
        private const val MAX_SYMBOLS = 8
        private val SYMBOL_POOL = listOf("△", "⬡", "□", "▭", "○", "◇", "★", "A")
    }
}
