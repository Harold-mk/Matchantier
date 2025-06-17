# MatChantier - Application de Gestion des Approvisionnements de Chantier

## 1. Présentation du Projet

### Qu'est-ce que MatChantier ?
MatChantier est une application de bureau développée en Java qui permet de gérer efficacement les approvisionnements et la logistique des chantiers de construction. Cette solution logicielle a été conçue pour répondre aux besoins spécifiques des gestionnaires de chantier en matière de suivi des stocks et des commandes de matériaux.

### Résumé des Fonctionnalités
- Gestion complète des stocks de matériaux
- Suivi des commandes et des livraisons
- Gestion des fournisseurs
- Génération de rapports et statistiques
- Interface graphique intuitive et conviviale

### Technologies Utilisées
- **Langage de Programmation** : Java 17
- **Base de Données** : SQLite
- **Framework GUI** : Java Swing
- **Gestion de Projet** : Maven
- **Bibliothèques Principales** :
  - SQLite JDBC Driver (3.42.0.0)
  - Apache Commons Lang (3.12.0)
  - JCalendar (1.4)
  - JUnit Jupiter (5.9.2)

## 2. Exigences Fonctionnelles

### Gestion des Stocks
- Suivi en temps réel des niveaux de stock
- Alertes de seuil minimum
- Historique des mouvements de stock
- Catégorisation des matériaux

### Gestion des Commandes
- Création et suivi des commandes
- Gestion des fournisseurs
- Suivi des délais de livraison
- Historique des commandes

### Gestion des Fournisseurs
- Base de données des fournisseurs
- Historique des transactions
- Évaluation des fournisseurs

### Rapports et Statistiques
- Génération de rapports de stock
- Statistiques d'utilisation
- Rapports de commandes
- Analyses de tendances

## 3. Structure Détaillée du Logiciel

### Architecture du Projet
```
matchantier/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/matchantier/
│   │   │       ├── controllers/    # Contrôleurs de l'application
│   │   │       ├── models/         # Modèles de données
│   │   │       ├── views/          # Interfaces graphiques
│   │   │       ├── utils/          # Utilitaires
│   │   │       └── Main.java       # Point d'entrée
│   │   └── resources/
│   │       └── icons/              # Ressources graphiques
├── matchantier.jar                 # Application exécutable
├── matchantier.db                  # Base de données SQLite
└── pom.xml                         # Configuration Maven
```

### Composants Principaux

#### Contrôleurs
- Gestion des interactions utilisateur
- Validation des données
- Coordination entre les modèles et les vues

#### Modèles
- Représentation des données
- Logique métier
- Interaction avec la base de données

#### Vues
- Interfaces graphiques
- Formulaires de saisie
- Tableaux de bord
- Rapports

#### Utilitaires
- Fonctions communes
- Gestion des erreurs
- Outils de validation

## 4. Guide d'Installation

### Prérequis Système
- Système d'exploitation : Windows 10/11, macOS, ou Linux
- Java Runtime Environment (JRE) version 17 ou supérieure
- 2 Go de RAM minimum
- 500 Mo d'espace disque disponible

### Étapes d'Installation

1. **Installation de Java**
   - Téléchargez Java JRE 17 depuis le site officiel d'Oracle
   - Exécutez l'installateur
   - Vérifiez l'installation en ouvrant un terminal :
     ```
     java -version
     ```

2. **Installation de MatChantier**
   - Créez un dossier pour l'application
   - Copiez les fichiers suivants dans ce dossier :
     - `matchantier.jar`
     - `matchantier.db`
     - `lancer-application.bat`

## 5. Guide d'Exécution

### Démarrage de l'Application
1. Double-cliquez sur le fichier `lancer-application.bat`
   OU
2. Ouvrez un terminal dans le dossier de l'application et exécutez :
   ```
   java -jar matchantier.jar
   ```

### Première Utilisation
1. L'application se lance avec l'écran de connexion
2. Utilisez les identifiants par défaut fournis dans le rapport de projet
3. Changez le mot de passe à la première connexion

### En Cas de Problème
- Vérifiez que Java est correctement installé
- Assurez-vous que tous les fichiers sont présents dans le même dossier
- Consultez le fichier de log pour plus de détails
- Consultez le rapport de projet pour les solutions aux problèmes courants

## Développement
Pour contribuer au développement du projet :

1. Clonez le dépôt
2. Assurez-vous d'avoir Maven installé
3. Exécutez `mvn clean install` pour construire le projet
4. Les tests peuvent être exécutés avec `mvn test`

## Support
Pour toute question ou problème, veuillez consulter le fichier "Rapport de Projet - Application MatChantier" inclus dans le projet.

## Licence
Ce projet est propriétaire et son utilisation est soumise aux conditions définies dans le rapport de projet.

## Auteurs
- Développé dans le cadre d'un projet académique
- Pour plus de détails, consultez le rapport de projet inclus 