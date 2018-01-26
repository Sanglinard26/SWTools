/*
 * Creation : 25 janv. 2018
 */
package cdf;

public final class History {

    private final String date;
    private final String auteur;
    private final String score;
    private final String commentaire;

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

}
