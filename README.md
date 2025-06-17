# MatChantier - Votre Copain pour la Gestion de Chantier

## 1. Présentation du Projet

### C'est quoi MatChantier ?
MatChantier, c'est votre nouveau meilleur ami pour gérer vos chantiers ! C'est une application sympa qui vous aide à garder un œil sur vos stocks et vos commandes. Plus besoin de vous arracher les cheveux avec des piles de papiers ou de courir après vos fournisseurs - MatChantier s'en occupe pour vous !

### Ce qu'il fait pour vous
- Il garde un œil sur vos stocks (comme un petit ange gardien !)
- Il suit vos commandes (plus besoin de vous en faire !)
- Il gère vos fournisseurs (comme un vrai pro !)
- Il vous fait des rapports (clair et net !)
- Il est super facile à utiliser (promis, juré !)

### Les outils qu'il utilise
- Java 17 (parce qu'on aime le moderne !)
- SQLite (petit mais costaud !)
- Java Swing (pour une belle interface !)
- Maven (pour tout bien organiser !)
- Et quelques petits plus :
  - SQLite JDBC Driver (3.42.0.0)
  - Apache Commons Lang (3.12.0)
  - JCalendar (1.4)
  - JUnit Jupiter (5.9.2)

## 2. Ce qu'il sait faire

### Pour vos stocks
- Il surveille tout en temps réel
- Il vous prévient avant qu'il ne soit trop tard
- Il garde une trace de tout
- Il range tout bien proprement

### Pour vos commandes
- Il crée des commandes en un clic
- Il garde un œil sur vos fournisseurs
- Il suit les délais de livraison
- Il n'oublie rien

### Pour vos fournisseurs
- Il garde tous vos contacts
- Il suit toutes vos transactions
- Il vous aide à choisir les meilleurs

### Pour vos rapports
- Il vous montre l'état de vos stocks
- Il vous dit comment ça évolue
- Il vous montre vos commandes
- Il vous aide à anticiper

## 3. Comment il est fait

### Sa structure
```
matchantier/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/matchantier/
│   │   │       ├── controllers/    # Le chef d'orchestre
│   │   │       ├── models/         # Le cerveau
│   │   │       ├── views/          # La belle façade
│   │   │       ├── utils/          # Les petits outils
│   │   │       └── Main.java       # Le point de départ
│   │   └── resources/
│   │       └── icons/              # Les jolies images
├── matchantier.jar                 # L'application
├── matchantier.db                  # La mémoire
└── pom.xml                         # La recette
```

### Ses différentes parties

#### Les contrôleurs
- Ils gèrent tout ce que vous faites
- Ils vérifient que tout est correct
- Ils font le lien entre tout

#### Les modèles
- Ils organisent vos données
- Ils font les calculs
- Ils parlent à la base de données

#### Les vues
- Elles vous montrent tout
- Elles sont faciles à utiliser
- Elles sont jolies à regarder

#### Les utilitaires
- Ils aident partout
- Ils gèrent les erreurs
- Ils vérifient que tout est bon

## 4. Comment l'installer

### Ce qu'il vous faut
- Un ordinateur qui marche (Windows, Mac ou Linux)
- Java 17 ou plus récent

### C'est parti !

1. **Installer Java**
   - Téléchargez Java 17 (c'est gratuit !)
   - Installez-le (c'est facile !)
   - Vérifiez que ça marche :
     ```
     java -version
     ```

2. **Installer MatChantier**
   - Créez un dossier
   - Mettez-y ces trois fichiers :
     - `matchantier.jar`
     - `matchantier.db`
     - `lancer-application.bat`

## 5. Comment l'utiliser

### Pour le démarrer
1. Double-cliquez sur `lancer-application.bat`
   OU
2. Ouvrez un terminal et tapez :
   ```
   java -jar matchantier.jar
   ```

### La première fois
1. L'écran de connexion apparaît
2. Utilisez les identifiants par défaut (ils sont dans le rapport)
3. Changez votre mot de passe (c'est important !)

### Si quelque chose ne marche pas
- Java est bien installé ?
- Tous les fichiers sont là ?
- Regardez le fichier de log
- Consultez le rapport de projet

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
