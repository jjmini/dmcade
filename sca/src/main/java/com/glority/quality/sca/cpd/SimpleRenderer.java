/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package com.glority.quality.sca.cpd;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.util.StringUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

public class SimpleRenderer implements Renderer {

    private String separator;
    private boolean trimLeadingWhitespace;

    public static final String DEFAULT_SEPARATOR = "=====================================================================";

    public SimpleRenderer() {
        this(false);
    }

    public SimpleRenderer(boolean trimLeadingWhitespace) {
        this(DEFAULT_SEPARATOR);
        this.trimLeadingWhitespace = trimLeadingWhitespace;
    }

    public SimpleRenderer(String theSeparator) {
        separator = theSeparator;
    }

    private void renderOn(StringBuilder rpt, Match match, OutputStream os) throws IOException,ReportException{
        rpt.append("Found a ").append(match.getLineCount()).append(" line (")
                .append(match.getTokenCount())
                .append(" tokens) duplication in the following files: ")
                .append(PMD.EOL);

        for (Iterator<TokenEntry> occurrences = match.iterator(); occurrences
                .hasNext();) {
            TokenEntry mark = occurrences.next();
            rpt.append("Starting at line ").append(mark.getBeginLine())
                    .append(" of ").append(mark.getTokenSrcID())
                    .append(PMD.EOL);
        }

        rpt.append(PMD.EOL); // add a line to separate the source from the desc
                             // above

        String source = match.getSourceCodeSlice();

        if (trimLeadingWhitespace) {
            String[] lines = source.split("[" + PMD.EOL + "]");
            int trimDepth = StringUtil.maxCommonLeadingWhitespaceForAll(lines);
            if (trimDepth > 0) {
                lines = StringUtil.trimStartOn(lines, trimDepth);
            }
            for (int i = 0; i < lines.length; i++) {
                rpt.append(lines[i]).append(PMD.EOL);
            }
            return;
        }
        rpt.append(source).append(PMD.EOL);
        os.write(rpt.toString().getBytes());
    }

    public String render(Iterator<Match> matches, File outputFile) throws ReportException, IOException{
        OutputStream os = new FileOutputStream(outputFile);
        StringBuilder rpt = new StringBuilder(300);

        if (matches.hasNext()) {
            renderOn(rpt, matches.next(), os);
            rpt = new StringBuilder(300);
        }

        Match match;
        while (matches.hasNext()) {
            match = matches.next();
            rpt.append(separator).append(PMD.EOL);
            renderOn(rpt, match, os);
            rpt = new StringBuilder(300);
        }
        os.close();
        return rpt.toString();
    }
}
