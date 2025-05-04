package tn.esprit.educareer.services;

import java.util.HashMap;
import java.util.Map;

public class SalaryAPI {


    private static final Map<String, Map<String, Double>> salaryData = new HashMap<>();

    static {

        Map<String, Double> tunisSalaries = new HashMap<>();
        tunisSalaries.put("Développeur", 2530.0);
        tunisSalaries.put("Designer", 2002.0);
        tunisSalaries.put("Chef de projet", 3800.0);

        Map<String, Double> franceSalaries = new HashMap<>();
        franceSalaries.put("Développeur", 2550.0);
        franceSalaries.put("Designer", 2000.0);
        franceSalaries.put("Chef de projet", 3560.0);

        Map<String, Double> dubaiSalaries = new HashMap<>();
        dubaiSalaries.put("Développeur", 2400.0);
        dubaiSalaries.put("Designer", 2100.0);
        dubaiSalaries.put("Chef de projet", 3500.0);

        Map<String, Double> sfaxSalaries = new HashMap<>();
        sfaxSalaries.put("Développeur", 2210.0);
        sfaxSalaries.put("Designer", 1800.0);
        sfaxSalaries.put("Chef de projet", 3230.0);

        salaryData.put("tunis", tunisSalaries);
        salaryData.put("france", franceSalaries);
        salaryData.put("dubai", dubaiSalaries);
        salaryData.put("sfax", sfaxSalaries);

    }

    //  le salaire moyen
    public static Double getAverageSalary(String poste, String ville) {
        if (ville == null || poste == null) {
            return null;
        }
        ville = ville.trim();
        poste = poste.trim();
        Map<String, Double> citySalaries = salaryData.get(ville);
        if (citySalaries != null) {
            return citySalaries.getOrDefault(poste, null);
        }
        return null;
    }
}
