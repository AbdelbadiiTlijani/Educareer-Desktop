package tn.esprit.educareer.controllers.seance;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import tn.esprit.educareer.models.Holiday;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class CalendarificAPI {

    public static List<Holiday> getHolidays(String apiKey, String country, String year) {
        try {
            String apiUrl = String.format("https://calendarific.com/api/v2/holidays?&api_key=%s&country=%s&year=%s&language=fr", apiKey, country, year);

            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder responseContent = new StringBuilder();
            String line;

            while ((line = in.readLine()) != null) {
                responseContent.append(line);
            }
            in.close();

            // Parse JSON
            ObjectMapper mapper = new ObjectMapper();
            ResponseWrapper wrapper = mapper.readValue(responseContent.toString(), ResponseWrapper.class);

            return wrapper.getResponse().getHolidays();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ResponseWrapper {
        private Response response;

        public Response getResponse() {
            return response;
        }

        public void setResponse(Response response) {
            this.response = response;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Response {
        private List<Holiday> holidays;

        public List<Holiday> getHolidays() {
            return holidays;
        }

        public void setHolidays(List<Holiday> holidays) {
            this.holidays = holidays;
        }
    }
}
