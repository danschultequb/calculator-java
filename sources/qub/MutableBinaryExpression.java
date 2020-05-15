package qub;

public class MutableBinaryExpression implements Expression
{
    private Expression leftExpression;
    private BinaryOperator operator;
    private Expression rightExpression;

    private MutableBinaryExpression()
    {
    }

    public static MutableBinaryExpression create()
    {
        return new MutableBinaryExpression();
    }

    public static MutableBinaryExpression create(double leftExpression, BinaryOperator operator, double rightExpression)
    {
        PreCondition.assertNotNull(operator, "operator");

        return MutableBinaryExpression.create(
            NumberExpression.create(leftExpression),
            operator,
            NumberExpression.create(rightExpression));
    }

    public static MutableBinaryExpression create(double leftExpression, BinaryOperator operator, Expression rightExpression)
    {
        PreCondition.assertNotNull(operator, "operator");
        PreCondition.assertNotNull(rightExpression, "rightExpression");

        return MutableBinaryExpression.create(
            NumberExpression.create(leftExpression),
            operator,
            rightExpression);
    }

    public static MutableBinaryExpression create(Expression leftExpression, BinaryOperator operator, double rightExpression)
    {
        PreCondition.assertNotNull(leftExpression, "leftExpression");
        PreCondition.assertNotNull(operator, "operator");

        return MutableBinaryExpression.create(
            leftExpression,
            operator,
            NumberExpression.create(rightExpression));
    }

    public static MutableBinaryExpression create(Expression leftExpression, BinaryOperator operator, Expression rightExpression)
    {
        PreCondition.assertNotNull(leftExpression, "leftExpression");
        PreCondition.assertNotNull(operator, "operator");
        PreCondition.assertNotNull(rightExpression, "rightExpression");

        return MutableBinaryExpression.create()
            .setLeftExpression(leftExpression)
            .setOperator(operator)
            .setRightExpression(rightExpression);
    }

    public Expression getLeftExpression()
    {
        return this.leftExpression;
    }

    public MutableBinaryExpression setLeftExpression(Expression leftExpression)
    {
        PreCondition.assertNotNull(leftExpression, "leftExpression");

        this.leftExpression = leftExpression;

        return this;
    }

    public BinaryOperator getOperator()
    {
        return this.operator;
    }

    public MutableBinaryExpression setOperator(BinaryOperator operator)
    {
        PreCondition.assertNotNull(operator, "operator");

        this.operator = operator;

        return this;
    }

    public Expression getRightExpression()
    {
        return this.rightExpression;
    }

    public MutableBinaryExpression setRightExpression(Expression rightExpression)
    {
        PreCondition.assertNotNull(rightExpression, "rightExpression");

        this.rightExpression = rightExpression;

        return this;
    }

    @Override
    public String toString()
    {
        return Objects.toString(this.leftExpression) + Objects.toString(this.operator) + Objects.toString(this.rightExpression);
    }

    @Override
    public boolean equals(Object rhs)
    {
        return rhs instanceof MutableBinaryExpression && this.equals((MutableBinaryExpression)rhs);
    }

    public boolean equals(MutableBinaryExpression rhs)
    {
        return rhs != null &&
            Comparer.equal(this.leftExpression, rhs.leftExpression) &&
            Comparer.equal(this.operator, rhs.operator) &&
            Comparer.equal(this.rightExpression, rhs.rightExpression);
    }

    @Override
    public Expression simplify()
    {
        Expression result;

        final Expression simplifiedLeftExpression = Expression.simplify(this.leftExpression);
        final Expression simplifiedRightExpression = Expression.simplify(this.rightExpression);
        if (simplifiedLeftExpression instanceof NumberExpression && simplifiedRightExpression instanceof NumberExpression)
        {
            final NumberExpression leftNumberExpression = (NumberExpression)simplifiedLeftExpression;
            final double leftNumber = leftNumberExpression.getValue().await();

            final NumberExpression rightNumberExpression = (NumberExpression)simplifiedRightExpression;
            final double rightNumber = rightNumberExpression.getValue().await();

            final double resultNumber = this.operator.run(leftNumber, rightNumber);
            result = NumberExpression.create(resultNumber);
        }
        else if (simplifiedLeftExpression != this.leftExpression || simplifiedRightExpression != this.rightExpression)
        {
            result = MutableBinaryExpression.create()
                .setLeftExpression(simplifiedLeftExpression)
                .setOperator(this.operator)
                .setRightExpression(simplifiedRightExpression);
        }
        else
        {
            result = this;
        }

        PostCondition.assertNotNull(result, "result");

        return result;
    }
}
