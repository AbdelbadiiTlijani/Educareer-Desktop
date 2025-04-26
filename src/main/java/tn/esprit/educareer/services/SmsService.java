package tn.esprit.educareer.services;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;


public class SmsService {
    private static final String ACCOUNT_SID = "AC87a65fb22263606bf291523cf8769884";
    private static final String AUTH_TOKEN = "101c219850a2093d61a6f8dea9ef543c";
    private static final String FROM_NUMBER = "+17245588033";

    private static final String ADMIN_PHONE_NUMBER = "+21694819660";

    // Initialize Twilio client
    static {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }


    public static String notifyAdminFormateurRegistration(String formateurName) {
        try {
            String messageBody = "Nouveau formateur ajouté : " + formateurName + "\n\nIl est actuellement en attente d'approbation.";

            Message message = Message.creator(
                            new PhoneNumber(ADMIN_PHONE_NUMBER),
                            new PhoneNumber(FROM_NUMBER),
                            messageBody)
                    .create();

            System.out.println("SMS sent successfully! SID: " + message.getSid());
            return message.getSid();
        } catch (Exception e) {
            System.err.println("Error sending SMS: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }


    public static String sendCustomSms(String phoneNumber, String messageBody) {
        try {
            Message message = Message.creator(
                            new PhoneNumber(phoneNumber),
                            new PhoneNumber(FROM_NUMBER),
                            messageBody)
                    .create();

            System.out.println("SMS sent successfully! SID: " + message.getSid());
            return message.getSid();
        } catch (Exception e) {
            System.err.println("Error sending SMS: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
