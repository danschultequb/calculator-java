package qub;

public interface QubCalculator
{
    static void main(String[] args)
    {
        PreCondition.assertNotNull(args, "args");

        DesktopProcess.run(args, QubCalculator::getParameters, QubCalculator::run);
    }

    static QubCalculatorParameters getParameters(DesktopProcess process)
    {
        PreCondition.assertNotNull(process, "process");

        final CommandLineParameters parameters = process.createCommandLineParameters()
            .setApplicationName("qub-calculator")
            .setApplicationDescription("Evaluate mathematical expressions and print the result.");
        final CommandLineParameterList<String> expressionList = parameters.addPositionStringList("expression")
            .setValueName("expression")
            .setDescription("The expression to evaluate.");
        final CommandLineParameterVerbose verboseParameter = parameters.addVerbose(process);
        final CommandLineParameterHelp helpParameter = parameters.addHelp();

        final Iterable<String> expressionParts = expressionList.getValues().await();
        helpParameter.setForceShowApplicationHelpLines(!expressionParts.any());

        QubCalculatorParameters result = null;
        if (!helpParameter.showApplicationHelpLines(process).await())
        {
            final CharacterWriteStream output = process.getOutputWriteStream();
            final VerboseCharacterToByteWriteStream verbose = verboseParameter.getVerboseCharacterToByteWriteStream().await();

            final String expressionString = Strings.join(' ', expressionParts);
            result = QubCalculatorParameters.create(output, verbose, expressionString);
        }
        return result;
    }

    static void run(QubCalculatorParameters parameters)
    {
        PreCondition.assertNotNull(parameters, "parameters");

        final CharacterWriteStream output = parameters.getOutput();
        final VerboseCharacterToByteWriteStream verbose = parameters.getVerbose();

        final String expressionString = parameters.getExpressionString();
        verbose.writeLine("Expression string: " + Strings.escapeAndQuote(expressionString)).await();

        final Expression expression = Expression.parse(expressionString).await();
        verbose.writeLine("Parsed expression: " + Strings.escapeAndQuote(expression.toString())).await();

        final Expression simplifiedExpression = expression.simplify();
        output.writeLine(simplifiedExpression.toString()).await();
    }
}
