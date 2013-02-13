package forge.error;


import forge.properties.NewConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.Map;
import java.util.Map.Entry;

import static forge.properties.ForgeProps.getLocalized;
import static forge.properties.ForgeProps.getProperty;
import static java.awt.event.InputEvent.CTRL_DOWN_MASK;
import static java.awt.event.KeyEvent.VK_S;
import static javax.swing.JOptionPane.DEFAULT_OPTION;
import static javax.swing.JOptionPane.ERROR_MESSAGE;


/**
 * The class ErrorViewer. Enables showing and saving error messages that occurred in forge.
 *
 * @author Clemens Koza
 * @version V1.0 02.08.2009
 */
public class ErrorViewer implements NewConstants, NewConstants.LANG.ErrorViewer {
    /** Constant <code>nameOS="os.name"</code> */
    private static final String nameOS = "os.name";
    /** Constant <code>versionOS="os.version"</code> */
    private static final String versionOS = "os.version";
    /** Constant <code>architectureOS="os.arch"</code> */
    private static final String architectureOS = "os.arch";
    /** Constant <code>versionJava="java.version"</code> */
    private static final String versionJava = "java.version";
    /** Constant <code>vendorJava="java.vendor"</code> */
    private static final String vendorJava = "java.vendor";

    /** Constant <code>ALL_THREADS_ACTION</code> */
    public static final Action ALL_THREADS_ACTION = new ShowAllThreadsAction();

    /**
     * Shows an error dialog taking the exception's message as the error message.
     *
     * @param ex a {@link java.lang.Throwable} object.
     */
    public static void showError(Throwable ex) {
        showError(ex, null);
    }

    /**
     * Shows an error dialog creating the error message by a formatting operation.
     *
     * @param ex a {@link java.lang.Throwable} object.
     * @param format a {@link java.lang.String} object.
     * @param args a {@link java.lang.Object} object.
     */
    public static void showError(Throwable ex, String format, Object... args) {
        if (ex == null) return;
        showError(ex, String.format(format, args));
    }

    /**
     * Shows an error dialog with the specified error message.
     *
     * @param ex a {@link java.lang.Throwable} object.
     * @param message a {@link java.lang.String} object.
     */
    public static void showError(final Throwable ex, String message) {
        if (ex == null) return;

        final StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        printError(pw, ex, message);

        showDialog(sw.toString());
    }

    /**
     * Shows an error without an exception that caused it.
     *
     * @param format a {@link java.lang.String} object.
     * @param args a {@link java.lang.Object} object.
     */
    public static void showError(String format, Object... args) {
        showError(String.format(format, args));
    }

    /**
     * Shows an error without an exception that caused it.
     *
     * @param message a {@link java.lang.String} object.
     */
    public static void showError(String message) {
        showError(new Exception(), message);
    }

    /**
     * Shows an error message for all running threads.
     *
     * @param format a {@link java.lang.String} object.
     * @param args a {@link java.lang.Object} object.
     */
    public static void showErrorAllThreads(String format, Object... args) {
        showErrorAllThreads(String.format(format, args));
    }

    /**
     * Shows an error message for all running threads.
     *
     * @param message a {@link java.lang.String} object.
     */
    public static void showErrorAllThreads(String message) {
        final StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        printError(pw, message);

        showDialog(sw.toString());
    }

    /**
     * <p>showDialog.</p>
     *
     * @param fullMessage a {@link java.lang.String} object.
     */
    private static void showDialog(String fullMessage) {
        JTextArea area = new JTextArea(fullMessage, 40, 90);
        area.setFont(new Font("Monospaced", Font.PLAIN, 10));
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);

        //Button is not modified, String gets the automatic listener to hide the dialog 
        Object[] options = {
                new JButton(new SaveAction(area)), getLocalized(BUTTON_CLOSE), new JButton(new ExitAction())};
        JOptionPane pane = new JOptionPane(new JScrollPane(area), ERROR_MESSAGE, DEFAULT_OPTION, null, options,
                options[1]);
        JDialog dlg = pane.createDialog(null, getLocalized(TITLE));
        dlg.setResizable(true);
        dlg.setVisible(true);
        dlg.dispose();
    }

    /**
     * Prints the error message for the specified exception to the print writer
     *
     * @param pw a {@link java.io.PrintWriter} object.
     * @param ex a {@link java.lang.Throwable} object.
     * @param message a {@link java.lang.String} object.
     */
    private static void printError(PrintWriter pw, Throwable ex, String message) {
        if (message != null) System.err.println(message);
        ex.printStackTrace();

        pw.printf(getLocalized(MESSAGE), getProperty(FORUM), getProperty(MAIL),
                message != null ? message : ex.getMessage(), getProperty(VERSION),
                System.getProperty(nameOS), System.getProperty(versionOS), System.getProperty(architectureOS),
                System.getProperty(versionJava), System.getProperty(vendorJava));
        ex.printStackTrace(pw);
    }

    /**
     * Prints the error message to the print writer, showing all running threads' stack traces.
     *
     * @param pw a {@link java.io.PrintWriter} object.
     * @param message a {@link java.lang.String} object.
     */
    private static void printError(PrintWriter pw, String message) {
        System.err.println(message);

        pw.printf(getLocalized(MESSAGE), getProperty(FORUM), getProperty(MAIL), message, getProperty(VERSION),
                System.getProperty(nameOS), System.getProperty(versionOS), System.getProperty(architectureOS),
                System.getProperty(versionJava), System.getProperty(vendorJava));
        Map<Thread, StackTraceElement[]> traces = Thread.getAllStackTraces();
        for (Entry<Thread, StackTraceElement[]> e : traces.entrySet()) {
            pw.println();
            pw.printf("%s (%s):%n", e.getKey().getName(), e.getKey().getId());
            for (StackTraceElement el : e.getValue()) {
                pw.println(el);
            }
        }
    }

    private static class SaveAction extends AbstractAction {

        private static final long serialVersionUID = 9146834661273525959L;

        private static JFileChooser c;

        private JTextArea area;

        public SaveAction(JTextArea area) {
            super(getLocalized(BUTTON_SAVE));
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(VK_S, CTRL_DOWN_MASK));
            this.area = area;
        }

        public void actionPerformed(ActionEvent e) {
            if (c == null) c = new JFileChooser();

            File f;
            for (int i = 0; ; i++) {
                String name = String.format("%TF-%02d.txt", System.currentTimeMillis(), i);
                f = new File(name);
                if (!f.exists()) break;
            }
            c.setSelectedFile(f);
            c.showSaveDialog(null);
            f = c.getSelectedFile();

            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(f));
                bw.write(area.getText());
                bw.close();
            } catch (IOException ex) {
                showError(ex, getLocalized(ERRORS.SAVE_MESSAGE));
            }
        }
    }

    private static class ExitAction extends AbstractAction {

        private static final long serialVersionUID = 276202595758381626L;

        public ExitAction() {
            super(getLocalized(BUTTON_EXIT));
        }


        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    }

    private static class ShowAllThreadsAction extends AbstractAction {

        private static final long serialVersionUID = 5638147106706803363L;

        public ShowAllThreadsAction() {
            super(getLocalized(SHOW_ERROR));
        }

        public void actionPerformed(ActionEvent e) {
            showErrorAllThreads(getLocalized(ERRORS.SHOW_MESSAGE));
        }
    }
}
