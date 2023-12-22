# Sauvgarde

# Documentation de l'Application de Sauvegarde et Restauration Sécurisée

## Vue d'ensemble
Cette application, basée sur Java et utilisant SSL pour la communication sécurisée, se concentre sur la sauvegarde et la restauration de fichiers. Elle offre une gestion des fichiers individuels et des fichiers zip.

## Fonctionnalités

### 1. Application Client (`client.Client`)
- **Connexion SSL**: Utilise SSL pour une communication sécurisée avec le serveur.
- **Interface Utilisateur**:
    - Permet de saisir un chemin de dossier pour la sauvegarde ou la restauration.
    - Choix entre sauvegarde et restauration.
    - Option de compression zip.
- **Sauvegarde**:
    - Sauvegarde de fichiers individuels ou répertoires.
    - Filtre basé sur les extensions dans `parameters.txt`.
    - Encodage en Base64 avant l'envoi au serveur.
- **Restauration**:
    - Restaure des fichiers ou répertoires depuis le serveur.
    - Support de la restauration depuis des fichiers zip.
    - Décode les fichiers en Base64 reçus du serveur.
- **Détection de Modifications**: Utilise des sommes de contrôle et comparaison d'octets pour vérifier les modifications.

### 2. Utilitaires Communs
- **Sauvegarde de fichiers** (`common.FileBackup`): Classe pour le transfert de données de fichiers.
- **Utilitaire Zip** (`common.ZipUtility`): Méthodes pour zipper et dézipper des répertoires.

### 3. Application Serveur
- **Serveur Principal** (`server.Server`): Initialise et gère un serveur SSL.
- **Gestionnaire de Clients** (`server.ClientHandler`): Traite les demandes de sauvegarde et de restauration, et gère le chiffrement AES.
- **Chargeur de Configuration** (`server.ConfigLoader`): Charge les propriétés de configuration.
- **Générateur de Clés** (`server.KeyGeneratorUtil`): Gère la création de clés AES.

### 4. Configuration SSL
- Gestion des clés SSL et des registres de confiance pour les connexions sécurisées.

### 5. Processus de Sauvegarde et de Restauration
- **Sauvegarde**: Transfert et stockage sécurisé des données par le serveur.
- **Restauration**: Récupération et restauration des données par le client.

### 6. Gestion des Erreurs et Journalisation
- Gestion basique des erreurs et affichage des messages pour diverses opérations.

### 7. Chiffrement et Déchiffrement
- Utilise le cryptage AES pour la sécurité des fichiers.

### 8. Traitement des Fichiers
- Gestion des chemins d'accès, création de répertoires, et lecture/écriture de contenu.

### 9. Traitement des Fichiers Zip
- Fonctionnalités pour la sauvegarde et restauration via fichiers zip.

### 10. Interface de Ligne de Commande
- Interface textuelle pour l'interaction avec l'application.

### 11. Sécurité
- Implémentation de SSL/TLS et AES pour la sécurité des communications et des fichiers.


## 2eme partie du TD : Les sauvegardes


## Première Partie: Les Sauvegardes via NAS

### Qu'est-ce qu'un NAS ?
- **NAS (Network Attached Storage)**: Dispositif de stockage connecté au réseau, permettant le stockage et l'accès aux données au sein d'un réseau local ou via Internet.

### Comment fonctionne un NAS ?
- Fonctionne comme un serveur de fichiers indépendant.
- Accessible via le réseau, offrant un espace de stockage centralisé.
- Gère la redondance des données pour la sécurité.

### Protocoles Réseaux Utilisés
- Principalement SMB/CIFS, NFS, AFP, et iSCSI.

### Avantages et Inconvénients
- **Avantages**:
    - Accès facile et partagé aux données.
    - Redondance et sauvegarde des données.
    - Extensibilité et polyvalence.
- **Inconvénients**:
    - Coût initial élevé.
    - Dépendance à la stabilité du réseau.

### Principaux Fournisseurs
- Synology, QNAP, Western Digital, Netgear, entre autres.

### Budget à Prévoir
- Varie de quelques centaines à plusieurs milliers d'euros, selon la capacité et les fonctionnalités.

## Deuxième Partie: Système RAID

### Qu'est-ce qu'un RAID ?
- **RAID (Redundant Array of Independent Disks)**: Technologie de stockage qui combine plusieurs disques durs en une seule unité logique.
- Offre redondance mais n'est pas un système de sauvegarde à proprement parler.

### Fonctionnement du RAID
- Répartit les données sur plusieurs disques.
- Améliore la performance et/ou la redondance des données.

### Solutions RAID et Récupération des Données
- **Types**: RAID 0, 1, 5, 6, 10, etc.
- Récupération complexe en cas de défaillance de plusieurs disques.

### Coûts des Solutions RAID
- Varient selon le niveau de RAID et le nombre de disques utilisés.

## Troisième Partie: Sauvegardes à Distance

### Sauvegarde à Distance
- **Définition**: Stockage des données sur un emplacement distant via Internet.
- Protège contre les sinistres locaux.

### Fonctionnement de la Sauvegarde à Distance
- Transfert des données via Internet vers un emplacement distant.
- Généralement géré par un logiciel de sauvegarde.

### Sauvegarde à Distance vs Cloud
- **Sauvegarde à Distance**: Stockage sur un serveur distant spécifique.
- **Cloud**: Utilise des services de stockage en ligne, souvent multi-tenant et élastiques.

### Principaux Outils de Sauvegarde à Distance
- Exemples : Veeam, Acronis, Mozy.

### Outils de Sauvegarde Cloud
- Exemples : Dropbox, Google Drive, Amazon S3, Microsoft Azure.



