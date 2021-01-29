package mx.com.sfinx;

import java.awt.image.BufferedImage;

public class App {

    private BufferedImage bufferedImage;
    private byte[] bytesImage;

    public static void main(String[] args) {
        App app = new App();
        Camera camera = new Camera(app);
        camera.setVisible(true);
    }

    public App() {
    }

    public void print() {
        String nombre = "Alejandro Salinas Arteaga";
        String fechaInicio = "10/12/2020";
        String fechaCaducidad = "10/12/2021";
        String curp = "EOLM940803HTCSRN00";
        String telefono = "5539991423";

        PrintCard printCard = new PrintCard("XPS Card Printer");
        printCard.printCards1(nombre, fechaInicio, fechaCaducidad, curp, telefono, bufferedImage);
    }

    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }

    public void setBufferedImage(BufferedImage bufferedImage) {
        System.out.println("Setted buffered image");
        this.bufferedImage = bufferedImage;
    }

    public byte[] getBytesImage() {
        return bytesImage;
    }

    public void setBytesImage(byte[] bytesImage) {
        System.out.println("Setted bytes image");
        this.bytesImage = bytesImage;
    }
}
