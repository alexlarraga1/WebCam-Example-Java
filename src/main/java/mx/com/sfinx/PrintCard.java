package mx.com.sfinx;

import javax.imageio.ImageIO;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterJob;
import java.net.URL;

public class PrintCard implements Printable {
    private final double POINTS_PER_INCH = 72;
    private String mPrinterName = null;
    private boolean mDuplex;
    private boolean mLandscape;
    private boolean mDoBacksideEncode;
    private boolean mEmbossCard;
    private int mCopyCount;
    private boolean mBlockPrintMagstripe;
    private boolean mBlockPrintSmartcard;
    private boolean mJISMagstripe;

    private String Nombre_u="";
    private String F_Creacion="";
    private String F_Epiracion="";
    private String Curp_U="";
    private String Tel_u="";
    private BufferedImage Imagen_Ruta;

    public PrintCard(String aPrinterName) {
        mPrinterName = aPrinterName;
        mDuplex = false;
        mDoBacksideEncode = false;
        mEmbossCard = false;
        mBlockPrintSmartcard = false;
        mBlockPrintMagstripe = false;
        mJISMagstripe = false;
        mLandscape =true;
        mCopyCount = 1;
    }

    private PrintService GetPrinterService() {
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(
                null, null);

        for (PrintService printer : printServices) {
            if (printer.getName().compareToIgnoreCase(mPrinterName) == 0) {
                System.out.println("Found Printer: " + printer.getName());
                return printer;
            }
        }
        System.out.println("Did not match any printer name as : " + mPrinterName);
        return null;
    }

    public void printCards1(String Nombre, String fechacrea, String fechavence, String curp, String telefono, BufferedImage RutaImagen) {
        PrintService printService = GetPrinterService();
        if (printService == null) {
            System.out.println("Error");
            return;
        }

        try {
            Nombre_u=Nombre;
            F_Creacion=fechacrea;
            F_Epiracion=fechavence;
            Curp_U=curp;
            Tel_u=telefono;
            Imagen_Ruta=RutaImagen;

            PrinterJob printJob = PrinterJob.getPrinterJob();
            printJob.setPrintService(printService);

            PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
            //agregar orientacion
            if (mLandscape) {
                aset.add(OrientationRequested.LANDSCAPE);
            } else {
                aset.add(OrientationRequested.PORTRAIT);
            }//agregar area y origen en pulgadas
            aset.add(new MediaPrintableArea(0.0f, 0.0f, 53.98f, 85.6f,
                    MediaSize.MM));
            aset.add(new Copies(mCopyCount));

            if (mDuplex) {
                aset.add(Sides.DUPLEX);
                mDuplex = true;
            } else {
                aset.add(Sides.ONE_SIDED);
            }

            printJob.setPrintable(this);
            printJob.print(aset);



        } catch (Exception PrintException) {
            PrintException.printStackTrace();
        }
    }


    @Override
    public int print(Graphics g, PageFormat pageFormat, int pageNumber) {
        int imgStartX =  15;
        int imgStartY =  22;

        // --- Translate the origin to be (0,0)
        Graphics2D graphics2D = (Graphics2D) g;
        graphics2D.setFont(new Font(null, Font.PLAIN, 8));

        graphics2D.translate(pageFormat.getImageableX(),pageFormat.getImageableY());


        final int printerDPI = 800;

        double pageWidth = pageFormat.getWidth() * printerDPI / POINTS_PER_INCH;
        double pageHeight = pageFormat.getHeight() * printerDPI / POINTS_PER_INCH;
        if (pageNumber == 0) {
            graphics2D.setColor(Color.black);
            Line2D.Double line = new Line2D.Double();
            graphics2D.drawString(Nombre_u, (int) 95, (int) 39);
            graphics2D.drawString(F_Creacion, (int) 95, (int) 60);
            graphics2D.drawString(F_Epiracion, (int) 170, (int) 60);
            graphics2D.drawString(Curp_U, (int) 95, (int) 83);
            graphics2D.drawString(Tel_u, (int) 95, (int) 106);

            // draw a color bitmap

            BufferedImage imag = Imagen_Ruta;//getSystemImage(Imagen_Ruta);
            int ancho = imag.getWidth();
            int alto = imag.getHeight();

            graphics2D.scale(1.2,1.15);
            drawImage(graphics2D, pageFormat,Imagen_Ruta , imgStartX,imgStartY, (int) pageWidth, (int) pageHeight);
            return (Printable.PAGE_EXISTS);
        } else {
            return (NO_SUCH_PAGE);
        }
    }


    private void drawImage(Graphics2D graphics2D, PageFormat pageFormat,
                           BufferedImage aFileName, int x, int y, int pageWidth, int pageHeight) {

        //BufferedImage img = getSystemImage(aFileName);
        BufferedImage img = aFileName;
        if (img == null) {
            return;
        }

        int w = img.getWidth();
        int h = img.getHeight();

        int destW = (int) (w * 0.3) + x;
        int destH = (int) (h * 0.3) + y;
        System.out.println(w);
        System.out.println(h);
        if (w > pageWidth) {
            destW = pageWidth;
        }

        if (h > pageHeight) {
            destH = pageHeight;
        }

        System.out.println(destW);
        System.out.println(destH);
        graphics2D.drawImage(img, x, y, destW, destH, 0, 0, w, h, null);
    }

    private BufferedImage getSystemImage(String filename) {
        if ((filename == null) || (filename.length() == 0)) {
            return null;
        }

        URL url = ClassLoader.getSystemResource(filename);
        if (url == null) {
            System.out.println("no se encuentra ruta: " + filename);
            return null;
        }

        try {
            return ImageIO.read(ClassLoader.getSystemResourceAsStream(filename));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
