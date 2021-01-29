package mx.com.sfinx;

import com.sun.jna.Library;
import com.sun.jna.Native;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;

public class Printer {
    public final static int BUFFSIZE = 10240;
    public final static String INTEROPDLL = "dxp01sdk_IBidiSpl_interop.dll";
    public interface XpsDriverInteropLib extends Library {

        XpsDriverInteropLib INSTANCE = (XpsDriverInteropLib) Native.loadLibrary(
                INTEROPDLL, XpsDriverInteropLib.class);

        int GetSDKVersion(String printerName, byte returnBuffer[],
                          int bufferSize[]);
        int GetPrinterOptions2(String printerName, byte returnBuffer[],
                               int bufferSize[]);
        /*
                int GetPrinterCounterStatus2(String printerName, byte returnBuffer[],
                                             int bufferSize[]);

                int GetPrinterSuppliesStatus(String printerName, byte returnBuffer[],
                                             int bufferSize[]);

                int StartJob(String printerName, byte returnXML[], int sizeOfReturnXML[]);

                int MagstripeRead(String printerName, byte dataBuffer[], int bufferSize[]);

                int MagstripeEncode(String printerName, String track1Data,
                                    int sizeOfTrack1Data, String track2Data, int sizeOfTrack2Data,
                                    String track3Data, int sizeOfTrack3Data, byte returnXML[],
                                    int sizeOfReturnXML[]);

                boolean SendResponseToPrinter(String aPrinterName, int command,
                                              int printerJobID, int errorCode);

                int SmartCardPark(
                        String aPrinterName,
                        byte returnXML[],
                        int sizeOfReturnXML[],
                        boolean parkBack);

                int SCardConnect(String aPrinterName, int connectType, int protocol[]);

                int SCardDisConnect(String aPrinterName, int disposition);

                int SCardStatus(
                        String aPrinterName,
                        int states[],
                        int numberStatesReturned[],
                        int protocol[],
                        byte returnATRBytes[],
                        int numATRBytesReturned[]);

                int SCardGetAttrib(String aPrinterName, int attrID,
                                   byte attrBytesBuffer[], int attrBytesBufferSize[]);

                int SCardTransmit(String aPrinterName, byte sendBytesBuffer[],
                                  int sendBytesBufferSize, byte receivedBytesBuffer[],
                                  int receivedBytesBufferSize[]);

                int ResumeJob(String printerName, int printerJobID, int errorCode,
                              byte returnXML[], int sizeOfReturnXML[]);

                int CancelJob(String printerName, int printerJobID, int errorCode,
                              byte returnXML[], int sizeOfReturnXML[]);

                int EndJob(String aPrinterName);

                int GetJobStatusXML(String aPrinterName, int aPrinterJobID,
                                    byte returnXML[], int sizeOfReturnXML[]);
        */
        int GetPrinterStatus(String schemaString, String printerName,
                             byte returnBuffer[], int bufferSize[]);

    }
    static {
        System.setProperty("jna.library.path", ".;" + System.getProperty("os.arch"));
        extractDll(System.getProperty("os.arch"), INTEROPDLL);

    }

    private static void DisplayInteropDllName(String dllName) {
        Path basePath = FileSystems.getDefault().getPath(dllName);
        Path absolutePath = basePath.toAbsolutePath();
        System.out.format(" basePath: %s\n absolutePath: %s",
                basePath,
                absolutePath);
        System.out.println();
    }

    private static void extractDll(String path, String name) {
        try {
            String resourcePath = path + "/" + name;
            URL url = ClassLoader.getSystemResource(resourcePath);
            if (url == null) {
                System.out.println("DLL no localizado.");
                DisplayInteropDllName(resourcePath);
                System.exit(1);
            }
            InputStream in = ClassLoader.getSystemResourceAsStream(resourcePath);
            OutputStream os = new FileOutputStream(name);


            byte[] buffer = new byte[BUFFSIZE];
            int length;
            while ((length = in.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
            os.close();
            in.close();
        } catch (Exception e) {
            System.out.format("extractDll() error: %s. Reason: %s", INTEROPDLL, e.getMessage());
            System.exit(0);
        }
    }
}
