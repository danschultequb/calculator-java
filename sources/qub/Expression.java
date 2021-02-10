package qub;

/**
 * A mathematical expression.
 */
public interface Expression
{
    /**
     * Simplify this mathematical expression.
     * @return The simplified version of this mathematical expression.
     */
    Expression simplify();

    /**
     * Simplify the provided mathematical expression or return null if the provided expression is
     * null.
     * @param expression The mathematical expression to simplify.
     * @return The simplified version of the provided mathematical expression, or null if the
     * provided expression was null.
     */
    static Expression simplify(Expression expression)
    {
        return expression == null ? null : expression.simplify();
    }

    /**
     * Parse a mathematical expression from the provided text.
     * @param text The text to parse.
     * @return The parsed mathematical expression.
     */
    static Result<Expression> parse(String text)
    {
        PreCondition.assertNotNull(text, "text");

        return Expression.parse(Strings.iterate(text));
    }

    /**
     * Parse a mathematical expression from the provided characters.
     * @param characters The characters to parse.
     * @return The parsed mathematical expression.
     */
    static Result<Expression> parse(Iterator<Character> characters)
    {
        PreCondition.assertNotNull(characters, "characters");

        return Result.create(() ->
        {
            characters.start();

            if (!characters.hasCurrent())
            {
                throw new ParseException("Missing expression.");
            }

            Expression completeExpression = null;
            final Stack<MutableBinaryExpression> incompleteExpressionStack = Stack.create();

            while (characters.hasCurrent())
            {
                if (Expression.isNumberStartCharacter(characters.getCurrent()))
                {
                    final NumberExpression numberExpression = Expression.parseNumberExpression(characters).await();
                    if (completeExpression != null)
                    {
                        throw new ParseException("Expected operator, but found number (" + numberExpression.toString() + ") instead.");
                    }
                    completeExpression = numberExpression;
                }
                else if (Expression.isBinaryOperatorStartCharacter(characters.getCurrent()))
                {
                    final BinaryOperator currentOperator = Expression.parseBinaryOperator(characters).await();
                    if (completeExpression == null)
                    {
                        throw new ParseException("Expected number, but found operator (" + currentOperator.toString() + ") instead.");
                    }

                    if (!incompleteExpressionStack.any())
                    {
                        incompleteExpressionStack.push(MutableBinaryExpression.create()
                            .setLeftExpression(completeExpression)
                            .setOperator(currentOperator));
                        completeExpression = null;
                    }
                    else
                    {
                        final BinaryOperator previousOperator = incompleteExpressionStack.peek().await().getOperator();
                        if (previousOperator.getPrecedence() >= currentOperator.getPrecedence())
                        {
                            final MutableBinaryExpression previousExpression = incompleteExpressionStack.pop().await();
                            previousExpression.setRightExpression(completeExpression);
                            completeExpression = previousExpression;
                        }

                        incompleteExpressionStack.push(MutableBinaryExpression.create()
                                .setLeftExpression(completeExpression)
                                .setOperator(currentOperator));
                        completeExpression = null;
                    }
                }
                else if (Characters.isWhitespace(characters.getCurrent()))
                {
                    characters.next();
                }
                else
                {
                    throw new ParseException("Unrecognized expression character: " + characters.getCurrent());
                }
            }

            while (incompleteExpressionStack.any())
            {
                final MutableBinaryExpression incompleteExpression = incompleteExpressionStack.pop().await();
                if (completeExpression == null)
                {
                    throw new ParseException("Missing right-hand side number for " + incompleteExpression.toString() + ".");
                }
                completeExpression = incompleteExpression.setRightExpression(completeExpression);
            }

            final Expression result = completeExpression;

            PostCondition.assertNotNull(result, "result");

            return result;
        });
    }

    /**
     * Get whether or not the provided character is the start character for a NumberExpression.
     * @param character The character to check.
     * @return Whether or not the provided character is the start character for a
     * NumberExpression.
     */
    static boolean isNumberStartCharacter(char character)
    {
        return character == '.' || Characters.isDigit(character);
    }

    /**
     * Parse a NumberExpression from the provided characters.
     * @param characters The characters to parse a NumberExpression from.
     * @return The parsed NumberExpression.
     */
    static Result<NumberExpression> parseNumberExpression(Iterator<Character> characters)
    {
        PreCondition.assertNotNull(characters, "characters");

        return Result.create(() ->
        {
            characters.start();

            if (!characters.hasCurrent())
            {
                throw new ParseException("Missing NumberExpression start character ('.' or digit).");
            }
            if (!Expression.isNumberStartCharacter(characters.getCurrent()))
            {
                throw new ParseException("Expected NumberExpression start character ('.' or digit), but found " + Characters.escapeAndQuote(characters.getCurrent()) + " instead.");
            }

            final CharacterList numberCharacters = CharacterList.create();
            while (characters.hasCurrent() && Characters.isDigit(characters.getCurrent()))
            {
                // Add integer value character.
                numberCharacters.add(characters.takeCurrent());
            }

            if (characters.hasCurrent() && characters.getCurrent() == '.')
            {
                // Add decimal point.
                numberCharacters.add(characters.takeCurrent());

                if (!characters.hasCurrent())
                {
                    throw new ParseException("Missing NumberExpression fractional digit character.");
                }

                while (characters.hasCurrent() && Characters.isDigit(characters.getCurrent()))
                {
                    // Add fractional value character.
                    numberCharacters.add(characters.takeCurrent());
                }
            }

            final String numberString = numberCharacters.toString(true);
            final NumberExpression result = NumberExpression.create(numberString);

            PostCondition.assertNotNull(result, "result");

            return result;
        });
    }

    /**
     * Get whether or not the provided character is the start character for any of the recognized
     * BinaryOperators.
     * @param character The character to check.
     * @return Whether or not the provided character is the start character for any of the
     * recognized BinaryOperators.
     */
    static boolean isBinaryOperatorStartCharacter(char character)
    {
        final String characterString = Characters.toString(character);
        return BinaryOperator.operators.contains((BinaryOperator operator) -> Strings.startsWith(operator.toString(), characterString));
    }

    /**
     * Parse a BinaryOperator from the provided characters.
     * @param characters The characters to parse a BinaryOperator from.
     * @return The parsed BinaryOperator.
     */
    static Result<BinaryOperator> parseBinaryOperator(Iterator<Character> characters)
    {
        PreCondition.assertNotNull(characters, "characters");

        return Result.create(() ->
        {
            final Iterable<BinaryOperator> possibleOperators = BinaryOperator.operators;
            characters.start();

            if (!characters.hasCurrent())
            {
                throw new ParseException("Missing BinaryOperator start character (" + English.orList(possibleOperators.map(o -> o.toString().charAt(0))) + ").");
            }
            if (!Expression.isBinaryOperatorStartCharacter(characters.getCurrent()))
            {
                throw new ParseException("Expected BinaryOperator start character (" + English.orList(possibleOperators.map(o -> o.toString().charAt(0))) + "), but found " + Characters.escapeAndQuote(characters.getCurrent()) + " instead.");
            }

            final char operatorCharacter = characters.takeCurrent();
            final BinaryOperator result = possibleOperators.first((BinaryOperator possibleOperator) -> possibleOperator.toString().charAt(0) == operatorCharacter);

            if (result == null)
            {
                throw new ParseException("Unrecognized BinaryOperator: " + operatorCharacter);
            }

            return result;
        });
    }
}
