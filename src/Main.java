import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Main extends JFrame {

    private JButton jButtonCapture;
    private JButton jButtonPrint;
    private JButton jButtonReload;
    private JPanel jPanelCamera;
    private JComboBox jComboCameras;
    private JPanel jPanelBorder;

    private Executor executor = Executors.newSingleThreadExecutor();
    private AtomicBoolean initialized = new AtomicBoolean(false);
    private Webcam webcam = null;
    private WebcamPanel panel = null;
    private byte[] capture;

    private BufferedImage image;

    private Logger log = LoggerFactory.getLogger(Main.class);


    public static void main(String args[]) {
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Main authFrame = new Main();
                    authFrame.setLocationRelativeTo(null);
                    authFrame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public Main() {
        setDefaultLookAndFeelDecorated(true);

        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }


        initComponents();

        // Listar WEBCAM
        List<Webcam> webcamList = Webcam.getWebcams();

        // Imprimir Nombre de camaras
        int i = 0;
        for (Webcam w : webcamList) {
            i++;
            System.out.println("Camara " + i + ": " + w.getName());
            jComboCameras.addItem(w.getName());
        }

        // Setteo de camara 3
        webcam = Webcam.getWebcams().get(2);

        // Impresion de dimensiones
        for (Dimension d : webcam.getViewSizes()) {
            System.out.println("w: " + d.getWidth() + ", h: " + d.getHeight());
        }

        // Escoger siempre la Segunda w: 320.0, h: 240.0
//        webcam.setViewSize(webcam.getViewSizes()[1]);
//        panel = new WebcamPanel(webcam, false);
//        panel.setPreferredSize(webcam.getViewSize());
//        panel.setOpaque(true);
//        panel.setBackground(Color.BLACK);
//        panel.setBounds(0, 0, 400, 300);
//        jPanelCamera.add(panel);
//        if (initialized.compareAndSet(false, true)) {
//            executor.execute(() -> panel.start());
//        }
    }

    private void initComponents() {

        jPanelBorder = new JPanel();
        jPanelCamera = new JPanel();
        jButtonCapture = new JButton();
        jButtonPrint = new JButton();
        jButtonReload = new JButton();
        jComboCameras = new JComboBox();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        setTitle("Captura Facial");
        setResizable(false);

        jPanelCamera.setName(""); // NOI18N
        jPanelCamera.setPreferredSize(new java.awt.Dimension(400, 300));


        jPanelBorder.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255)));
        jPanelBorder.setBackground(new Color(0,0,0,0));
        jPanelBorder.setPreferredSize(new java.awt.Dimension(200, 300));

        GroupLayout jPanelCameraLayout = new GroupLayout(jPanelCamera);
        jPanelCamera.setLayout(jPanelCameraLayout);
        jPanelCameraLayout.setHorizontalGroup(
                jPanelCameraLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanelCameraLayout.setVerticalGroup(
                jPanelCameraLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGap(0, 300, Short.MAX_VALUE)
        );

        jButtonCapture.setText("Take photo");
        jButtonCapture.addActionListener(this::captureActionPerformed);

        jButtonPrint.setText("Get Bytes in console");
        jButtonPrint.addActionListener(this::printActionPerformed);

        jButtonReload.setText("Reload Camera");
        jButtonReload.addActionListener(this::reloadActionPerformed);

        jComboCameras.addItemListener(e -> {
            if(e.getStateChange() == ItemEvent.SELECTED){
                webcam = Webcam.getWebcamByName((String) e.getItem());
                webcam.setViewSize(webcam.getViewSizes()[1]);

                reloadCam();
            }
        });


        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jComboCameras)
                                .addContainerGap())
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jPanelCamera, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap())
                        .addGroup(layout.createSequentialGroup()
                                .addGap(29, 29, 29)
                                .addComponent(jButtonCapture)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 50, Short.MAX_VALUE)
                                .addComponent(jButtonReload)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 50, Short.MAX_VALUE)
                                .addComponent(jButtonPrint)
                                .addGap(44, 44, 44))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jComboCameras)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jPanelCamera, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(jButtonCapture)
                                        .addComponent(jButtonReload)
                                        .addComponent(jButtonPrint))
                                .addContainerGap())
        );

        pack();
    }

    private void reloadCam(){
        if(panel != null && panel.isStarted()){
            panel.stop();
            panel = null;
        }

        panel = new WebcamPanel(webcam, false);
        panel.setPreferredSize(webcam.getViewSize());
        panel.setOpaque(true);
        panel.setBackground(Color.BLACK);
        panel.setBounds(0, 0, 400, 300);
        panel.add(jPanelBorder);
        jPanelCamera.add(panel);
        executor.execute(() -> panel.start());
    }

    private void reloadActionPerformed(ActionEvent evt){
        reloadCam();
    }

    private void printActionPerformed(ActionEvent evt) {
        System.out.println(Base64.getEncoder().encodeToString(capture));
    }

    private void captureActionPerformed(ActionEvent evt) {
        ByteArrayOutputStream baos = null;
        try {

            // Setedo de Buffered Image
            image = crop(webcam.getImage(), new Rectangle(85, 0, 170, 320));

            // Conversion a PNG
            //Nombre y formato de la imagen de salida
            baos = new ByteArrayOutputStream();
            ImageIO.write(image, "PNG", baos);//new File("webcam_test.png"));
            baos.flush();
            capture = baos.toByteArray();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                baos.close();
                panel.stop();
                jPanelCamera.remove(panel);

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private BufferedImage crop(BufferedImage src, Rectangle rect) {
        BufferedImage dest = new BufferedImage((int) rect.getWidth(), (int) rect.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
        Graphics g = dest.getGraphics();
        g.drawImage(src, 0, 0, (int) rect.getWidth(), (int) rect.getHeight(), (int) rect.getX(), (int) rect.getY(), (int) (rect.getX() + rect.getWidth()), (int) (rect.getY() + rect.getHeight()), null);
        g.dispose();

        return dest;
    }
}
