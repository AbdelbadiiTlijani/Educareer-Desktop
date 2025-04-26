package tn.esprit.educareer.controllers.Event;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

public class QRCodePopupController {

    @FXML
    private ImageView qrCodeImageView;

    public void setQRCodeData(String data) {
        try {
            // Générer le QR code
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, 300, 300);

            // Convertir en BufferedImage
            BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

            // Convertir BufferedImage en Image JavaFX
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", os);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(os.toByteArray());
            Image fxImage = new Image(inputStream);

            // Afficher dans l'ImageView
            qrCodeImageView.setImage(fxImage);

        } catch (WriterException | IOException e) {
            e.printStackTrace();
        }
    }
}
