package tn.esprit.educareer.test;

import tn.esprit.educareer.models.CategorieProjet;
import tn.esprit.educareer.models.User;
import tn.esprit.educareer.services.ServiceCategorieProjet;
import tn.esprit.educareer.services.ServiceUser;
import tn.esprit.educareer.utils.MyConnection;

public class Main {
    public static void main (String args[] ) {
        MyConnection mycnx =  MyConnection.getInstance();

        ServiceCategorieProjet scp = new ServiceCategorieProjet();
        CategorieProjet cp = new CategorieProjet("test",1,0);
        scp.ajouter(cp);
        //scp.getAll();
        //scp.supprimer(cp);
        //scp.modifier(cp);

    }
}
