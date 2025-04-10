package tn.esprit.educareer.services;

import tn.esprit.educareer.interfaces.IService;
import tn.esprit.educareer.models.CategorieProjet;
import tn.esprit.educareer.utils.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceCategorieProjet implements IService<CategorieProjet> {


    Connection cnx ;
    public ServiceCategorieProjet(){
        cnx= MyConnection.getInstance().getCnx();
    }
    @Override
    public void ajouter(CategorieProjet categorieProjet) {
        try {
            String req = "INSERT INTO `categorie_projet`( `categorie`, `is_validated`, `is_deleted`) VALUES ('"
                    + categorieProjet.getCategorie() + "', '"
                    + categorieProjet.getIs_validated() + "', "
                    + categorieProjet.getIs_deleted() +  ");";


            Statement st = cnx.createStatement();
            st.executeUpdate(req);
            System.out.println("Catégorie ajoutée avec succès !");
        } catch (SQLException ex) {
            System.out.println("Erreur lors de l'ajout : " + ex.getMessage());
        }
    }

    @Override
    public void modifier(CategorieProjet categorieProjet) {

    }

    @Override
    public void supprimer(CategorieProjet categorieProjet) {

    }

    @Override
    public List<CategorieProjet> getAll() {
        List<CategorieProjet> lieux = new ArrayList<>();
        try {
            String req = "SELECT * FROM categorie_projet";
            PreparedStatement pst = cnx.prepareStatement(req);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                CategorieProjet cp = new CategorieProjet();
                cp.setId(rs.getInt("id"));  // Ensure this column exists
                cp.setCategorie(rs.getString("categorie"));
                cp.setIs_validated(rs.getInt("is_validated"));  // Use column name instead of index
                cp.setIs_deleted(rs.getInt("is_deleted"));  // Use column name instead of index

                lieux.add(cp);
                System.out.println("Retrieved categorie: " + cp.getCategorie() + ", validée ?: " + cp.getIs_validated() + ", deleted?: " + cp.getIs_deleted());
            }

            System.out.println("Total lieux found: " + lieux.size());

        } catch (SQLException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
        return lieux;}


    @Override
    public CategorieProjet getOne(CategorieProjet categorieProjet) {
        return null;
    }
}
