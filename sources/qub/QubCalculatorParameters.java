package qub;

/**
 * Parameters that can be passed to the QubCalculator application.
 */
public class QubCalculatorParameters
{
    private final CharacterWriteStream output;
    private final VerboseCharacterWriteStream verbose;
    private final String expressionString;

    private QubCalculatorParameters(CharacterWriteStream output, VerboseCharacterWriteStream verbose, String expressionString)
    {
        PreCondition.assertNotNull(output, "output");
        PreCondition.assertNotNull(verbose, "verbose");
        PreCondition.assertNotNullAndNotEmpty(expressionString, "expressionString");

        this.output = output;
        this.verbose = verbose;
        this.expressionString = expressionString;
    }

    /**
     * Create a new QubCalculatorParameters object with the provided parameters.
     * @param output The output CharacterWriteStream where the result will be printed to.
     * @param verbose The VerboseCharacterWriteStream where verbose output will be written to.
     * @param expressionString The full expression string that was passed on the command line.
     * @return A new QubCalculatorParameters object.
     */
    public static QubCalculatorParameters create(CharacterWriteStream output, VerboseCharacterWriteStream verbose, String expressionString)
    {
        return new QubCalculatorParameters(output, verbose, expressionString);
    }

    /**
     * Get the output CharacterWriteStream where the result will be printed to.
     * @return The output CharacterWriteStream where the result will be printed to.
     */
    public CharacterWriteStream getOutput()
    {
        return this.output;
    }

    /**
     * Get the VerboseCharacterWriteStream where verbose output will be written to.
     * @return The VerboseCharacterWriteStream where verbose output will be written to.
     */
    public VerboseCharacterWriteStream getVerbose()
    {
        return this.verbose;
    }

    /**
     * Get the expression string that was passed on the command line.
     * @return The expression string that was passed on the command line.
     */
    public String getExpressionString()
    {
        return this.expressionString;
    }
}
