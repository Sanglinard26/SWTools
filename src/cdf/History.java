/*
 * Creation : 25 janv. 2018
 */
package cdf;

public final class History {

    private String date;
    private String auteur;
    private String score;
    private String commentaire;

    public History(String date, String auteur, String score, String commentaire) {

        if (date != null) {
            this.date = date;
        } else {
            this.date = "";
        }

        if (auteur != null) {
            this.auteur = auteur;
        } else {
            this.auteur = "";
        }

        if (score != null) {
            this.score = score;
        } else {
            this.score = "";
        }

        if (commentaire != null) {
            this.commentaire = commentaire;
        } else {
            this.commentaire = "";
        }

    }

    public final String getDate() {
        return date;
    }

    public final String getAuteur() {
        return auteur;
    }

    public final String getScore() {
        return score;
    }

    public final String getCommentaire() {
        return commentaire;
    }
    
    public final void setDate(String date) {
        this.date = date;
    }

    public final void setAuteur(String auteur) {
        this.auteur = auteur;
    }

    public final void setScore(String score) {
        this.score = score;
    }

    public final void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }
    
    

}
