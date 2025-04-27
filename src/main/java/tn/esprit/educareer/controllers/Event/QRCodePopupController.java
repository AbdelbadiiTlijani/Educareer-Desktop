package tn.esprit.educareer.controllers.Event;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;

import java.awt.image.BufferedImage;

public class QRCodePopupController {

    @FXML
    private ImageView qrCodeImageView;

    public void setQRCodeData(String data) {
        if (data != null && !data.isEmpty()) {
            Image qrImage = generateQRCodeImage(data);
            if (qrImage != null) {
                qrCodeImageView.setImage(qrImage);
            }
        }
    }

    private Image generateQRCodeImage(String data) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, 300, 300);

            BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
            return SwingFXUtils.toFXImage(bufferedImage, null);
        } catch (WriterException | IllegalArgumentException e) {
            e.printStackTrace();
            showError("Erreur lors de la génération du QR code. Vérifiez les données.");
            return null;
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(message);
        alert.showAndWait();
    }

}
