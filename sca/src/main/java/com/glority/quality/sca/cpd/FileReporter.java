/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package com.glority.quality.sca.cpd;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import net.sourceforge.pmd.util.IOUtil;

/**
 * @author Philippe T'Seyen
 */
public class FileReporter {
    private File reportFile;
    private String encoding;

    public FileReporter(String encoding) {
        this(null, encoding);
    }

    public FileReporter(File reportFile) {
        this(reportFile, System.getProperty("file.encoding"));
    }

    public FileReporter(File reportFile, String encoding) {
        this.reportFile = reportFile;
        this.encoding = encoding;
    }

    public void report(String content) throws ReportException {
        try {
            Writer writer = null;
            try {
                OutputStream outputStream;
                if (reportFile == null) {
                    outputStream = System.out;
                } else {
                    outputStream = new FileOutputStream(reportFile);
                }
                writer = new BufferedWriter(new OutputStreamWriter(
                        outputStream, encoding));
                writer.write(content);
            } finally {
                IOUtil.closeQuietly(writer);
            }
        } catch (IOException ioe) {
            throw new ReportException(ioe);
        }
    }
}
