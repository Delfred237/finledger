package com.portfolio.finledger;

/**
 * Point d'entrée principal de l'application FinLedger CLI.
 *
 * Pour l'instant, cette classe affiche uniquement un message de démarrage.
 * Elle sera ensuite remplacée par un vrai lancement de l'interface CLI.
 */
public final class Main {

    /**
     * Constructeur privé.
     *
     * La classe Main n'a pas vocation à être instanciée.
     * Elle sert uniquement de point d'entrée statique.
     */
    private Main() {
        // Aucune instanciation nécessaire.
    }

    /**
     * Méthode main appelée par la JVM au lancement de l'application.
     *
     * @param args arguments de la ligne de commande, non utilisés pour l'instant
     */
    public static void main(String[] args) {
        System.out.println("FinLedger CLI v0.1.0");
        System.out.println("Initial project skeleton - next step: domain model.");
    }
}