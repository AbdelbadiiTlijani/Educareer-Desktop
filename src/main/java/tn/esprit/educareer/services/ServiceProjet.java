package tn.esprit.educareer.services;

import tn.esprit.educareer.interfaces.IService;
import tn.esprit.educareer.models.Projet;
import tn.esprit.educareer.utils.MyConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class ServiceProjet implements IService<Projet> {


    Connection cnx ;
    public ServiceProjet(){
        cnx= MyConnection.getInstance().getCnx();
    }
    @Override
    public void ajouter(Projet projet) {
        try {
            String req = "INSERT INTO `projet`(`categorie_id`, `titre`, `contenu`, `formateur_id`) VALUES ('"
                    + projet.getCategorie_id() + "', '"
                    + projet.getTitre() + "', "
                    + projet.getContenu() + "', "
                    +6  + "');";
//                    + projet.getFormateur_id() + ");";


            Statement st = cnx.createStatement();
            st.executeUpdate(req);
            System.out.println("Lieu ajouté avec succès !");
        } catch (SQLException ex) {
            System.out.println("Erreur lors de l'ajout : " + ex.getMessage());
        }
    }



    @Override
    public void modifier(Projet projet) {

    }

    @Override
    public void supprimer(Projet projet) {

    }

    @Override
    public List<Projet> getAll() {
        return null;
    }

    @Override
    public Projet getOne(Projet projet) {
        return null;
    }
}
