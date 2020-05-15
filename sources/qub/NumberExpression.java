package qub;

public class NumberExpression implements Expression
{
    private final String text;

    private NumberExpression(String text)
    {
        PreCondition.assertNotNullAndNotEmpty(text, "text");

        this.text = text;
    }

    /**
     * Create a new NumberExpression from the provided value.
     * @param value The number value to create a NumberExpression from.
     * @return The new NumberExpression.
     */
    public static NumberExpression create(double value)
    {
        String numberString = Doubles.toString(value);
        if (numberString.endsWith(".0"))
        {
            numberString = numberString.substring(0, numberString.length() - 2);
        }
        return NumberExpression.create(numberString);
    }

    /**
     * Create a new NumberExpression from the provided String.
     * @param text The String to create a NumberExpression from.
     * @return The new NumberExpression.
     */
    public static NumberExpression create(String text)
    {
        return new NumberExpression(text);
    }

    /**
     * Get the parsed numeric value of this expression.
     * @return The parsed numeric value of this expression.
     */
    public Result<Double> getValue()
    {
        return Doubles.parse(this.text);
    }

    @Override
    public NumberExpression simplify()
    {
        return this;
    }

    @Override
    public String toString()
    {
        return this.text;
    }

    @Override
    public boolean equals(Object rhs)
    {
        return rhs instanceof NumberExpression && this.equals((NumberExpression)rhs);
    }

    /**
     * Get whether or not this NumberExpression is equal to the provided NumberExpression.
     * @param rhs The NumberExpression to compare to this NumberExpression.
     * @return Whether or not this NumberExpression is equal to the provided NumberExpression.
     */
    public boolean equals(NumberExpression rhs)
    {
        return rhs != null &&
            (Comparer.equal(this.text, rhs.text) ||
             Comparer.equal(this.getValue().await(), rhs.getValue().await()));
    }
}
