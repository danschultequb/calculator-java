package qub;

public class BinaryOperator
{
    public static final BinaryOperator plus = BinaryOperator.create("+", 100, (Double lhs, Double rhs) -> lhs + rhs);
    public static final BinaryOperator minus = BinaryOperator.create("-", 100, (Double lhs, Double rhs) -> lhs - rhs);
    public static final BinaryOperator times = BinaryOperator.create("*", 200, (Double lhs, Double rhs) -> lhs * rhs);
    public static final BinaryOperator dividedBy = BinaryOperator.create("/", 200, (Double lhs, Double rhs) -> lhs / rhs);

    public static final Iterable<BinaryOperator> operators = Iterable.create(
        BinaryOperator.plus,
        BinaryOperator.minus,
        BinaryOperator.times,
        BinaryOperator.dividedBy);

    private final String text;
    private final int precedence;
    private final Function2<Double,Double,Double> function;

    private BinaryOperator(String text, int precedence, Function2<Double,Double,Double> function)
    {
        PreCondition.assertNotNullAndNotEmpty(text, "text");
        PreCondition.assertNotNull(function, "function");

        this.text = text;
        this.precedence = precedence;
        this.function = function;
    }

    /**
     * Create a new Operator.
     * @param text The text of the Operator.
     * @param precedence The precedence of the Operator. The actual value doesn't matter. This value is used
     * relative to other operators to determine whether or not an operator has higher precedence
     * than other operators.
     * @param function The function that will invoke the operator on two numbers.
     * @return The new Operator.
     */
    public static BinaryOperator create(String text, int precedence, Function2<Double,Double,Double> function)
    {
        PreCondition.assertNotNullAndNotEmpty(text, "text");

        return new BinaryOperator(text, precedence, function);
    }

    /**
     * The precedence for this operator. The actual value doesn't matter. This value is used
     * relative to other operators to determine whether or not an operator has higher precedence
     * than other operators.
     * @return The precedence for this operator.
     */
    public int getPrecedence()
    {
        return this.precedence;
    }

    /**
     * Apply/run this operator on the provided inputs.
     * @param lhs The left-hand-side of the binary expression.
     * @param rhs The right-hand-side of the binary expression.
     * @return The result of running this operator on the provided inputs.
     */
    public double run(Double lhs, Double rhs)
    {
        PreCondition.assertNotNull(lhs, "lhs");
        PreCondition.assertNotNull(rhs, "rhs");

        return this.function.run(lhs, rhs);
    }

    @Override
    public String toString()
    {
        return this.text;
    }

    @Override
    public boolean equals(Object rhs)
    {
        return rhs instanceof BinaryOperator && this.equals((BinaryOperator)rhs);
    }

    /**
     * Get whether or not this Operator is equal to the provided Operator.
     * @param rhs The Operator to compare to this Operator.
     * @return Whether or not this Operator is equal to the provided Operator.
     */
    public boolean equals(BinaryOperator rhs)
    {
        return rhs != null && this.text.equals(rhs.text);
    }
}
