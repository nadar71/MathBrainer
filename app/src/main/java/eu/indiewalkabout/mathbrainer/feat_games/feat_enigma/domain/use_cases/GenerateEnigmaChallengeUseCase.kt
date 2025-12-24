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
            equations += buildEquation(
                newSymbol = symbol,
                newValue = symbolValue,
                knownSymbol = values.keys.random(random),
                values = values,
                operators = availableOperators,
                random = random,
                level = level
            )
        }

        val expressionSymbols = symbols.shuffled(random).take(expressionSymbolCount(level, symbolCount))
        val finalExpression = buildFinalExpression(
            level = level,
            symbols = expressionSymbols,
            values = values,
            operators = availableOperators,
            random = random
        )

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
        values: Map<String, Int>,
        operators: List<Operator>,
        random: Random,
        level: Int
    ): EnigmaEquation {
        if (level >= 4 && values.size >= 2 && random.nextBoolean()) {
            buildCompositeEquation(
                newSymbol = newSymbol,
                newValue = newValue,
                values = values,
                operators = operators,
                random = random
            )?.let { return it }
        }

        val knownValue = values.getValue(knownSymbol)
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

    private fun buildCompositeEquation(
        newSymbol: String,
        newValue: Int,
        values: Map<String, Int>,
        operators: List<Operator>,
        random: Random
    ): EnigmaEquation? {
        val knownSymbols = values.keys.toList()
        if (knownSymbols.size < 2) return null

        val firstKnown = knownSymbols.random(random)
        val secondKnown = (knownSymbols - firstKnown).random(random)
        val firstValue = values.getValue(firstKnown)
        val secondValue = values.getValue(secondKnown)

        val firstOperator = pickSafeOperator(newValue, firstValue, operators, random)
        val intermediate = firstOperator.apply(newValue, firstValue)
        val secondOperator = pickSafeOperator(intermediate, secondValue, operators, random)
        val result = secondOperator.apply(intermediate, secondValue)

        val expression = "$newSymbol ${firstOperator.symbol} $firstKnown ${secondOperator.symbol} $secondKnown"
        return EnigmaEquation(expression, result)
    }

    private fun buildFinalExpression(
        level: Int,
        symbols: List<String>,
        values: Map<String, Int>,
        operators: List<Operator>,
        random: Random
    ): FinalExpressionResult {
        val multiTerm = symbols.size > 2
        val expressionOperators = buildExpressionOperators(level, operators, multiTerm)

        var expression = symbols.first()
        var result = values.getValue(symbols.first())

        symbols.drop(1).forEachIndexed { index, symbol ->
            val value = values.getValue(symbol)
            val operator = pickSafeOperator(result, value, expressionOperators, random)
            val useParentheses = shouldUseParentheses(level, index, symbols.size, multiTerm, random)

            if (useParentheses) {
                expression = "($expression ${operator.symbol} $symbol)"
            } else {
                expression += " ${operator.symbol} $symbol"
            }

            result = operator.apply(result, value)
        }

        return FinalExpressionResult(expression = "$expression = ?", answer = result)
    }
    private fun pickSafeOperator(
        current: Int,
        next: Int,
        operators: List<Operator>,
        random: Random
    ): Operator {
        val shuffled = operators.shuffled(random)
        val valid = shuffled.firstOrNull { operator -> isValidOperation(operator, current, next) }
        return valid ?: Operator.PLUS
    }

    private fun isValidOperation(operator: Operator, current: Int, next: Int): Boolean {
        return when (operator) {
            Operator.MINUS -> current - next >= 0
            Operator.DIVIDE -> next != 0 && current % next == 0
            else -> true
        }
    }

    private fun buildExpressionOperators(
        level: Int,
        operators: List<Operator>,
        multiTerm: Boolean
    ): List<Operator> {
        val expanded = when {
            level < 3 -> listOf(Operator.PLUS, Operator.MINUS)
            level < 5 -> listOf(Operator.PLUS, Operator.MINUS, Operator.MULTIPLY)
            else -> operators
        }

        return if (multiTerm && level < 4) {
            expanded.filter { it == Operator.PLUS || it == Operator.MINUS }
        } else {
            expanded
        }
    }

    private fun shouldUseParentheses(
        level: Int,
        index: Int,
        symbolSize: Int,
        multiTerm: Boolean,
        random: Random
    ): Boolean {
        if (!multiTerm || level < 6) return false
        if (index == symbolSize - 2) return random.nextBoolean()
        return index == 0 && random.nextInt(3) == 0
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
            level < 7 -> 4
            else -> 5
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
