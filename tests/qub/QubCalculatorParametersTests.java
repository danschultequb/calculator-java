package qub;

public interface QubCalculatorParametersTests
{
    static void test(TestRunner runner)
    {
        PreCondition.assertNotNull(runner, "runner");

        runner.testGroup(QubCalculatorParameters.class, () ->
        {
            runner.testGroup("create(CharacterWriteStream,String)", () ->
            {
                runner.test("with null output", (Test test) ->
                {
                    final CharacterWriteStream output = null;
                    final VerboseCharacterWriteStream verbose = new VerboseCharacterWriteStream(false, InMemoryCharacterStream.create());
                    final String expressionString = "1 + 2";
                    test.assertThrows(() -> QubCalculatorParameters.create(output, verbose, expressionString),
                        new PreConditionFailure("output cannot be null."));
                });

                runner.test("with null verbose", (Test test) ->
                {
                    final CharacterWriteStream output = InMemoryCharacterStream.create();
                    final VerboseCharacterWriteStream verbose = null;
                    final String expressionString = "1 + 2";
                    test.assertThrows(() -> QubCalculatorParameters.create(output, verbose, expressionString),
                        new PreConditionFailure("verbose cannot be null."));
                });

                runner.test("with null expressionString", (Test test) ->
                {
                    final CharacterWriteStream output = InMemoryCharacterStream.create();
                    final VerboseCharacterWriteStream verbose = new VerboseCharacterWriteStream(false, InMemoryCharacterStream.create());
                    final String expressionString = null;
                    test.assertThrows(() -> QubCalculatorParameters.create(output, verbose, expressionString),
                        new PreConditionFailure("expressionString cannot be null."));
                });

                runner.test("with empty expressionString", (Test test) ->
                {
                    final CharacterWriteStream output = InMemoryCharacterStream.create();
                    final VerboseCharacterWriteStream verbose = new VerboseCharacterWriteStream(false, InMemoryCharacterStream.create());
                    final String expressionString = "";
                    test.assertThrows(() -> QubCalculatorParameters.create(output, verbose, expressionString),
                        new PreConditionFailure("expressionString cannot be empty."));
                });

                runner.test("with non-empty expressionString", (Test test) ->
                {
                    final CharacterWriteStream output = InMemoryCharacterStream.create();
                    final VerboseCharacterWriteStream verbose = new VerboseCharacterWriteStream(false, InMemoryCharacterStream.create());
                    final String expressionString = "hello";
                    final QubCalculatorParameters parameters = QubCalculatorParameters.create(output, verbose, expressionString);
                    test.assertSame(output, parameters.getOutput());
                    test.assertEqual(expressionString, parameters.getExpressionString());
                });
            });
        });
    }
}
