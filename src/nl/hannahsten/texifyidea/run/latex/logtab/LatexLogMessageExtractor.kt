package nl.hannahsten.texifyidea.run.latex.logtab

import nl.hannahsten.texifyidea.run.latex.logtab.LatexLogMagicRegex.DUPLICATE_WHITESPACE
import nl.hannahsten.texifyidea.run.latex.logtab.LatexLogMagicRegex.TEX_MISC_WARNINGS
import nl.hannahsten.texifyidea.run.latex.logtab.messagehandlers.errors.LatexErrorHandler
import nl.hannahsten.texifyidea.run.latex.logtab.messagehandlers.errors.LatexSingleLineErrorMessageHandler
import nl.hannahsten.texifyidea.run.latex.logtab.messagehandlers.errors.LatexUndefinedControlSequenceHandler
import nl.hannahsten.texifyidea.run.latex.logtab.messagehandlers.warnings.*
import nl.hannahsten.texifyidea.util.remove
import nl.hannahsten.texifyidea.util.removeAll

object LatexLogMessageExtractor {
    /**
     * Pre-processing to check if line is worth looking at.
     */
    fun skip(text: String?): Boolean {
        return text.isNullOrBlank()
    }

    /**
     * Look for a warning or error message in [textWithNewlines], and return a handler that
     * can handle the warning (i.e., process it and output the correct log message).
     * Return null if [textWithNewlines] does not contain such an error or warning.
     *
     * @param textWithNewlines Text in which to search for error messages (will be two consecutive lines)
     * @param newText Second line of 'text',
     */
    fun findMessage(textWithNewlines: String, newText: String, currentFile: String?): LatexLogMessage? {
        val text = textWithNewlines.removeAll("\n", "\r")

        val specialErrorHandlersList = listOf(
            LatexUndefinedControlSequenceHandler,
            LatexSingleLineErrorMessageHandler
        )

        // Most of these are just to have a special regex to extract the right line number
        val specialWarningHandlersList = listOf(
            YouHaveRequestedOnInputLineVersionOfPackageWarningHandler,
            OverfullHboxWarningHandler,
            EndOccurredInsideGroupWarningHandler,
            EndOccurredWhenConditionWasIncompleteWarningHandler,
            LatexPackageWarningHandler,
            LatexReferenceCitationWarningHandler,
            LatexLineWarningHandler
        )

        // Look for errors that need special treatment.
        specialErrorHandlersList.forEach { handler ->
            if (handler.regex.any { it.containsMatchIn(text) }) {
                return handler.findMessage(text, newText, currentFile)
            }
        }

        // Handles all other file line errors. Only check the first line,
        // because other errors might need the two lines, and would be
        // (partly) duplicated in the log if we allow the fallback to inspect
        // the two lines (or just the first).
        if (LatexErrorHandler.regex.any { it.containsMatchIn(text.removeSuffix(newText.trim())) }) {
            return LatexErrorHandler.findMessage(text, newText, currentFile)
        }

        // Look for warnings that need special treatment.
        specialWarningHandlersList.forEach {
            if (it.regex.any { r -> r.containsMatchIn(text) }) {
                return it.findMessage(text, newText, currentFile)
            }
        }

        // Check if we have found a warning
        if (TEX_MISC_WARNINGS.any { text.removeSuffix(newText).startsWith(it) }) {
            var messageText = if (LatexOutputListener.isLineEndOfMessage(newText, text)) text.remove(newText) else text
            messageText = messageText.remove("LaTeX Warning:")
                .trim()
                // Improves readability, and at the moment we don't have an example where this would be incorrect
                .trim('(', ')', '[', ']', ' ')
                .replace(DUPLICATE_WHITESPACE.toRegex(), " ")

            if (LatexOutputListener.isLineEndOfMessage(newText, text.remove(newText.trim()))) {
                messageText = messageText.remove(newText.trim()).trim()
            }

            return LatexLogMessage(
                messageText,
                fileName = currentFile,
                type = LatexLogMessageType.WARNING
            )
        }

        return null
    }

    /*
    Can be useful. TODO remove when done
    re_loghead    = re.compile("This is [0-9a-zA-Z-]*(TeX|Omega)")
re_rerun      = re.compile("LaTeX Warning:.*Rerun")
re_file       = re.compile("(\\((?P<file>[^ \n\t(){}]*)|\\))")
re_badbox     = re.compile(r"(Ov|Und)erfull \\[hv]box ")
re_line       = re.compile(r"(l\.(?P<line>[0-9]+)( (?P<code>.*))?$|<\*>)")
re_cseq       = re.compile(r".*(?P<seq>\\[^ ]*) ?$")
re_page       = re.compile("\[(?P<num>[0-9]+)\]")
re_atline     = re.compile("( detected| in paragraph)? at lines? (?P<line>[0-9]*)(--(?P<last>[0-9]*))?")
re_reference  = re.compile("LaTeX Warning: Reference `(?P<ref>.*)' on page (?P<page>[0-9]*) undefined on input line (?P<line>[0-9]*)\\.$")
re_citation   = re.compile("^.*Citation `(?P<cite>.*)' on page (?P<page>[0-9]*) undefined on input line (?P<line>[0-9]*)\\.$")
re_label      = re.compile("LaTeX Warning: (?P<text>Label .*)$")
re_warning    = re.compile("(LaTeX|Package)( (?P<pkg>.*))? Warning: (?P<text>.*)$")
re_online     = re.compile("(; reported)? on input line (?P<line>[0-9]*)")
re_ignored    = re.compile("; all text was ignored after line (?P<line>[0-9]*).$")
     */
}