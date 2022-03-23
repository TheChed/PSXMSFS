// 
// Decompiled by Procyon v0.5.36
// 

package misc;

import util.StatusMonitor;
import java.awt.print.PrinterJob;
import java.awt.print.PrinterException;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.Graphics;
import network.DataFromPsxMain;
import util.ObservableData;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Lock;
import javax.swing.JTextField;
import java.awt.print.Printable;
import util.ObserverData;

public class PrinterService implements ObserverData, Printable
{
    private static JTextField textEmptyLines;
    private static JTextField textLeftAlign;
    private static JTextField textHeightAlign;
    private static String Qs119;
    private static String text;
    private static String textToPrint;
    private static final Lock lockQs119;
    private static int leftAlign;
    private static int heightAlign;
    private static int endLines;
    
    static {
        PrinterService.textEmptyLines = new JTextField();
        PrinterService.textLeftAlign = new JTextField();
        PrinterService.textHeightAlign = new JTextField();
        PrinterService.Qs119 = new String("");
        PrinterService.text = new String("");
        PrinterService.textToPrint = new String("");
        lockQs119 = new ReentrantLock();
        PrinterService.leftAlign = 0;
        PrinterService.heightAlign = 0;
        PrinterService.endLines = 0;
    }
    
    @Override
    public void updateObservers(final ObservableData argObs, final Object argId, final Object argData) {
        if (argObs instanceof DataFromPsxMain) {
            final String s;
            switch (s = (String)argId) {
                case "Qs119": {
                    PrinterService.lockQs119.lock();
                    PrinterService.Qs119 = (String)argData;
                    PrinterService.lockQs119.unlock();
                    this.printQs119();
                    break;
                }
                default:
                    break;
            }
        }
    }
    
    @Override
    public int print(final Graphics g, final PageFormat pf, final int page) throws PrinterException {
        PrinterService.leftAlign = Integer.parseInt(PrinterService.textLeftAlign.getText());
        PrinterService.heightAlign = Integer.parseInt(PrinterService.textHeightAlign.getText());
        PrinterService.endLines = Integer.parseInt(PrinterService.textEmptyLines.getText());
        if (page > 0) {
            return 1;
        }
        final Graphics2D g2d = (Graphics2D)g;
        g2d.translate(pf.getImageableX(), pf.getImageableY());
        while (PrinterService.textToPrint.indexOf(94) != -1) {
            g.drawString(PrinterService.textToPrint.substring(0, PrinterService.textToPrint.indexOf(94)), PrinterService.leftAlign, PrinterService.heightAlign);
            PrinterService.textToPrint = PrinterService.textToPrint.substring(PrinterService.textToPrint.indexOf(94) + 1);
            PrinterService.heightAlign += 15;
        }
        g.drawString(PrinterService.textToPrint, PrinterService.leftAlign, PrinterService.heightAlign);
        for (int i = 0; i < PrinterService.endLines; ++i) {
            PrinterService.heightAlign += 15;
            g.drawString("        ", PrinterService.leftAlign, PrinterService.heightAlign);
        }
        PrinterService.textToPrint = PrinterService.text;
        return 0;
    }
    
    public static void passJTextFields(final JTextField argTextEmptyLines, final JTextField argTextLeftAlign, final JTextField argIdTextHeightAlign) {
        PrinterService.textEmptyLines = argTextEmptyLines;
        PrinterService.textLeftAlign = argTextLeftAlign;
        PrinterService.textHeightAlign = argIdTextHeightAlign;
    }
    
    private void printQs119() {
        PrinterService.text = getQs119().substring(getQs119().indexOf(61) + 1);
        if (!PrinterService.text.isEmpty()) {
            PrinterService.textToPrint = PrinterService.text;
            final PrinterJob job = PrinterJob.getPrinterJob();
            job.setPrintable(this);
            if (StatusMonitor.getPrintOutputEnabled()) {
                try {
                    job.print();
                    StatusMonitor.setPrintOutputFailed(false);
                }
                catch (PrinterException e) {
                    StatusMonitor.setPrintOutputFailed(true);
                    e.printStackTrace();
                }
            }
        }
    }
    
    private static String getQs119() {
        PrinterService.lockQs119.lock();
        final String temp = new String(PrinterService.Qs119);
        PrinterService.lockQs119.unlock();
        return temp;
    }
}
