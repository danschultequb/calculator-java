package qub;

public interface QubCalculatorTests
{
    static void test(TestRunner runner)
    {
        runner.testGroup(QubCalculator.class, () ->
        {
            runner.testGroup("main(String[])", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    test.assertThrows(() -> QubCalculator.main((String[])null),
                        new PreConditionFailure("args cannot be null."));
                });
            });

            runner.testGroup("getParameters(QubProcess)", () ->
            {
                runner.test("with null Process", (Test test) ->
                {
                    test.assertThrows(() -> QubCalculator.getParameters(null),
                        new PreConditionFailure("process cannot be null."));
                });

                runner.test("with no command-line arguments", (Test test) ->
                {
                    try (final QubProcess process = QubProcess.create())
                    {
                        final InMemoryCharacterToByteStream output = InMemoryCharacterToByteStream.create();
                        process.setOutputWriteStream(output);

                        test.assertNull(QubCalculator.getParameters(process));

                        test.assertEqual(
                            Iterable.create(
                                "Usage: qub-calculator [[--expression=]expression] [--verbose] [--help]",
                                "  Evaluate mathematical expressions and print the result.",
                                "  --expression: The expression to evaluate.",
                                "  --verbose(v): Whether or not to show verbose logs.",
                                "  --help(?): Show the help message for this application."),
                            Strings.getLines(output.getText().await()));
                    }
                });

                runner.test("with " + Iterable.create("--help").map(Strings::escapeAndQuote), (Test test) ->
                {
                    try (final QubProcess process = QubProcess.create("--help"))
                    {
                        final InMemoryCharacterToByteStream output = InMemoryCharacterToByteStream.create();
                        process.setOutputWriteStream(output);

                        test.assertNull(QubCalculator.getParameters(process));

                        test.assertEqual(
                            Iterable.create(
                                "Usage: qub-calculator [[--expression=]expression] [--verbose] [--help]",
                                "  Evaluate mathematical expressions and print the result.",
                                "  --expression: The expression to evaluate.",
                                "  --verbose(v): Whether or not to show verbose logs.",
                                "  --help(?): Show the help message for this application."),
                            Strings.getLines(output.getText().await()));
                    }
                });

                runner.test("with " + Iterable.create("1").map(Strings::escapeAndQuote), (Test test) ->
                {
                    try (final QubProcess process = QubProcess.create("1"))
                    {
                        final InMemoryCharacterToByteStream output = InMemoryCharacterToByteStream.create();
                        process.setOutputWriteStream(output);

                        final QubCalculatorParameters parameters = QubCalculator.getParameters(process);
                        test.assertNotNull(parameters);
                        test.assertEqual("1", parameters.getExpressionString());
                        test.assertSame(output, parameters.getOutput());
                        test.assertNotNull(parameters.getVerbose());
                        test.assertFalse(parameters.getVerbose().isVerbose());

                        test.assertEqual(
                            Iterable.create(),
                            Strings.getLines(output.getText().await()));
                    }
                });

                runner.test("with " + Iterable.create("1+2").map(Strings::escapeAndQuote), (Test test) ->
                {
                    try (final QubProcess process = QubProcess.create("1+2"))
                    {
                        final InMemoryCharacterToByteStream output = InMemoryCharacterToByteStream.create();
                        process.setOutputWriteStream(output);

                        final QubCalculatorParameters parameters = QubCalculator.getParameters(process);
                        test.assertNotNull(parameters);
                        test.assertEqual("1+2", parameters.getExpressionString());
                        test.assertSame(output, parameters.getOutput());
                        test.assertNotNull(parameters.getVerbose());
                        test.assertFalse(parameters.getVerbose().isVerbose());

                        test.assertEqual(
                            Iterable.create(),
                            Strings.getLines(output.getText().await()));
                    }
                });

                runner.test("with " + Iterable.create("1 + 2").map(Strings::escapeAndQuote), (Test test) ->
                {
                    try (final QubProcess process = QubProcess.create("1 + 2"))
                    {
                        final InMemoryCharacterToByteStream output = InMemoryCharacterToByteStream.create();
                        process.setOutputWriteStream(output);

                        final QubCalculatorParameters parameters = QubCalculator.getParameters(process);
                        test.assertNotNull(parameters);
                        test.assertEqual("1 + 2", parameters.getExpressionString());
                        test.assertSame(output, parameters.getOutput());
                        test.assertNotNull(parameters.getVerbose());
                        test.assertFalse(parameters.getVerbose().isVerbose());

                        test.assertEqual(
                            Iterable.create(),
                            Strings.getLines(output.getText().await()));
                    }
                });

                runner.test("with " + Iterable.create("1", "+", "2").map(Strings::escapeAndQuote), (Test test) ->
                {
                    try (final QubProcess process = QubProcess.create("1", "+", "2"))
                    {
                        final InMemoryCharacterToByteStream output = InMemoryCharacterToByteStream.create();
                        process.setOutputWriteStream(output);

                        final QubCalculatorParameters parameters = QubCalculator.getParameters(process);
                        test.assertNotNull(parameters);
                        test.assertEqual("1 + 2", parameters.getExpressionString());
                        test.assertSame(output, parameters.getOutput());
                        test.assertNotNull(parameters.getVerbose());
                        test.assertFalse(parameters.getVerbose().isVerbose());

                        test.assertEqual(
                            Iterable.create(),
                            Strings.getLines(output.getText().await()));
                    }
                });
            });

            runner.testGroup("run(QubCalculatorParameters)", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    test.assertThrows(() -> QubCalculator.run(null),
                        new PreConditionFailure("parameters cannot be null."));
                });

                final Action2<String,String> runTest = (String expressionString, String expectedOutput) ->
                {
                    runner.test("with " + Strings.escapeAndQuote(expressionString), (Test test) ->
                    {
                        final InMemoryCharacterToByteStream output = InMemoryCharacterToByteStream.create();
                        final InMemoryCharacterStream verboseStream = InMemoryCharacterStream.create();
                        final VerboseCharacterWriteStream verbose = new VerboseCharacterWriteStream(false, verboseStream);

                        final QubCalculatorParameters parameters = QubCalculatorParameters.create(output, verbose, expressionString);

                        QubCalculator.run(parameters);

                        test.assertEqual(expectedOutput, output.getText().await());
                    });
                };

                runTest.run("1", "1\n");
                runTest.run("200", "200\n");
                runTest.run("1+2", "3\n");
                runTest.run("1 + 2", "3\n");
                runTest.run("1-2", "-1\n");
                runTest.run("1*2", "2\n");
                runTest.run("1/2", "0.5\n");
                runTest.run("1/0", "Infinity\n");
            });
        });
    }
}
