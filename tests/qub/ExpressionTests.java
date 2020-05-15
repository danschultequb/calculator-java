package qub;

public interface ExpressionTests
{
    static void test(TestRunner runner)
    {
        runner.testGroup(Expression.class, () ->
        {
            runner.testGroup("parse(String)", () ->
            {
                final Action2<String,Throwable> parseErrorTest = (String text, Throwable expected) ->
                {
                    runner.test("with " + Strings.escapeAndQuote(text), (Test test) ->
                    {
                        test.assertThrows(() -> Expression.parse(text).await(), expected);
                    });
                };

                parseErrorTest.run(null, new PreConditionFailure("text cannot be null."));
                parseErrorTest.run("", new ParseException("Missing expression."));

                final Action2<String,Expression> parseTest = (String text, Expression expected) ->
                {
                    runner.test("with " + Strings.escapeAndQuote(text), (Test test) ->
                    {
                        test.assertEqual(expected, Expression.parse(text).await());
                    });
                };

                parseTest.run("0", NumberExpression.create("0"));
                parseTest.run("3", NumberExpression.create("3"));
                parseTest.run("123", NumberExpression.create("123"));
                parseTest.run(
                    "1+2",
                    MutableBinaryExpression.create()
                        .setLeftExpression(NumberExpression.create(1))
                        .setOperator(BinaryOperator.plus)
                        .setRightExpression(NumberExpression.create(2)));
                parseTest.run(
                    "1+2+3",
                    MutableBinaryExpression.create()
                        .setLeftExpression(
                            MutableBinaryExpression.create()
                                .setLeftExpression(NumberExpression.create(1))
                                .setOperator(BinaryOperator.plus)
                                .setRightExpression(NumberExpression.create(2)))
                        .setOperator(BinaryOperator.plus)
                        .setRightExpression(NumberExpression.create(3)));
                parseTest.run(
                    "1+2-3",
                    MutableBinaryExpression.create()
                        .setLeftExpression(
                            MutableBinaryExpression.create()
                                .setLeftExpression(NumberExpression.create(1))
                                .setOperator(BinaryOperator.plus)
                                .setRightExpression(NumberExpression.create(2)))
                        .setOperator(BinaryOperator.minus)
                        .setRightExpression(NumberExpression.create(3)));
                parseTest.run(
                    "1+2*3",
                    MutableBinaryExpression.create()
                        .setLeftExpression(NumberExpression.create(1))
                        .setOperator(BinaryOperator.plus)
                        .setRightExpression(
                            MutableBinaryExpression.create()
                                .setLeftExpression(NumberExpression.create(2))
                                .setOperator(BinaryOperator.times)
                                .setRightExpression(NumberExpression.create(3))));
                parseTest.run(
                    "1*2-3",
                    MutableBinaryExpression.create()
                        .setLeftExpression(
                            MutableBinaryExpression.create()
                                .setLeftExpression(NumberExpression.create(1))
                                .setOperator(BinaryOperator.times)
                                .setRightExpression(NumberExpression.create(2)))
                        .setOperator(BinaryOperator.minus)
                        .setRightExpression(NumberExpression.create(3)));
                parseTest.run(
                    "1 * 4 / 2 + 7 * 3 - 8 + 16",
                    MutableBinaryExpression.create(
                        MutableBinaryExpression.create(
                            MutableBinaryExpression.create(
                                1,
                                BinaryOperator.times,
                                4),
                            BinaryOperator.dividedBy,
                            2),
                        BinaryOperator.plus,
                        MutableBinaryExpression.create()
                            .setLeftExpression(
                                MutableBinaryExpression.create()
                                    .setLeftExpression(MutableBinaryExpression.create(7, BinaryOperator.times, 3))
                                    .setOperator(BinaryOperator.minus)
                                    .setRightExpression(NumberExpression.create(8)))
                            .setOperator(BinaryOperator.plus)
                            .setRightExpression(NumberExpression.create(16))));
            });

            runner.testGroup("parse(Iterator<Character>)", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    test.assertThrows(() -> Expression.parse((Iterator<Character>)null),
                        new PreConditionFailure("characters cannot be null."));
                });
            });
        });
    }
}
