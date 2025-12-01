# Documentation de l'API PSI

## Table des matières
1. [Introduction](#introduction)
2. [Prérequis](#prérequis)
3. [Logique métier](#logique-métier)
4. [Spécifications techniques](#spécifications-techniques)
5. [Exemples d'utilisation](#exemples-dutilisation)
6. [Ressources supplémentaires](#ressources-supplémentaires)

---

## Introduction

L'API PSI permet d'enrôler des tiers dans le système PSI (Pro Santé Identité). Elle est conçue pour un usage interne et externe tels que le le RPPS et les établissements de santé afin d'enregistrer les professionnels en santé tout en validant la qualité des donnéés d'identité auprès du RNIPP.

L'API est exposée via **Gravitee** de l' ANS (Agence du Numérique en Santé) à l'URL suivante :  
**[https://psi-partenaire.gateway.api.esante.gouv.fr/](https://psi-partenaire.gateway.api.esante.gouv.fr/)**

Cette documentation fournit une vue d'ensemble de la logique métier, des spécifications techniques et des exemples d'utilisation pour faciliter l'intégration.

---

## Prérequis

Avant de commencer à utiliser l'API, assurez-vous de remplir les conditions suivantes :

- **Accès à l'API** : Vous devez disposer des droits d'accès à l'URL Gravitee mentionnée ci-dessus. Pour cela, merci de remplir le formulaire de demande dipsonible à l'URL suivante :
- **Authentification** : Les appels à l'API nécessitent une authentification. Les clés d'API ou les jetons nécessaires vous seront fournis par l'administrateur ANS de Gravitee.
- **Outils recommandés** :
  - Un outil pour tester les API REST, comme **Postman**.
  - Un lecteur de fichiers YAML pour consulter le fichier Swagger fourni.

---

## Logique métier

La logique métier de l'API est décrite dans le schéma suivant :


![Logique métier](PSI_Enrolement_par_un_tiers_(Logigramme).jpg)

### Résumé de la logique métier

1. **Recherche initiale** :
   - L'API commence par rechercher les traits d'identité dans le système PSI.
   - Si aucun compte PSI n'existe, une recherche est effectuée dans le RNIPP, le RPPS et l'API-PSC.

2. **Validation et enrôlement** :
   - Si les données sont valides et qu'une correspondance unique est trouvée, un compte PSI est créé ou mis à jour.
   - En cas de divergences ou de correspondances multiples, des informations sont retournées à l'appelant pour résolution.

3. **Cas spécifiques** :
   - Gestion des identifiants RPPS.
   - Création de comptes avec des données partielles (e-mail et téléphone non vérifiés).

---

## Spécifications techniques

### Endpoints disponibles

#### 1. **Créer ou mettre à jour une identité**
- **Méthode** : `POST`
- **URL** : `/v1/identities`
- **Description** : Permet de créer ou de mettre à jour une identité dans le système PSI.

##### Requête
- **Headers** :
  - `Content-Type: application/json`
  - `Authorization: Bearer <token>`
- **Body** (exemple) :
```json
{
  "lastName": "DUPONT",
  "firstNames": ["Jean", "Pierre"],
  "birthDate": "1990-01-01",
  "genderCode": "M",
  "birthLocationCode": "75056",
  "email": "jean.dupont@example.com",
  "phone": "0612345678",
  "identifier": "8100112345678"
}
```

##### Réponses
- **201 Created** : Identité créée avec succès.
- **400 Bad Request** : Erreur de validation des données (exemple : format de date incorrect).
- **404 Not Found** : Aucune correspondance trouvée dans le RNIPP.
- **409 Conflict** : Un compte PSI existe déjà pour ces traits d'identité.
- **500 Internal Server Error** : Erreur interne.

Pour plus de détails sur les schémas de requêtes et réponses, consultez le fichier Swagger : `PSI_swagger_15-10-25.yml`.

---

### Schémas des données

Voici les principaux schémas utilisés par l'API :

#### 1. **RegisterOrUpdateIdentityRequestDto**
- **Description** : Représente les données nécessaires pour créer ou mettre à jour une identité.
- **Propriétés** :
  - `lastName` (string) : Nom de famille.
  - `firstNames` (array of string) : Liste des prénoms.
  - `birthDate` (string) : Date de naissance (format ISO-8601).
  - `genderCode` (string) : Sexe (`M`, `F`, `I`).
  - `birthLocationCode` (string) : Code INSEE du lieu de naissance.
  - `email` (string) : Adresse e-mail.
  - `phone` (string) : Numéro de téléphone.
  - `identifier` (string) : Identifiant (optionnel)(ex. : identifiant RPPS).

#### 2. **RegisterOrUpdateIdentityResponseDto**
- **Description** : Réponse retournée après la création ou mise à jour d'une identité.
- **Propriétés** :
  - `rppsIdentifiers` (array of string) : Liste des identifiants RPPS associés.
  - `identityTraits` (object) : Traits d'identité trouvés.

Pour une liste complète des schémas, référez-vous au fichier Swagger.

---

## Exemples d'utilisation

### 1. Création d'une identité avec succès
**Requête** :
```bash
curl -X POST "https://apimgmtui.integ.api.esante.gouv.fr/v1/identities" \
-H "Content-Type: application/json" \
-H "Authorization: Bearer <token>" \
-d '{
  "lastName": "DUPONT",
  "firstNames": ["Jean", "Pierre"],
  "birthDate": "1990-01-01",
  "genderCode": "M",
  "birthLocationCode": "75056",
  "email": "jean.dupont@example.com",
  "phone": "0612345678",
  "identifier": "8100112345678"
}'
```

**Réponse** :
```json
{
  "rppsIdentifiers": ["8100112345678"],
  "identityTraits": {
    "lastName": "DUPONT",
    "firstNames": ["Jean", "Pierre"],
    "birthDate": "1990-01-01",
    "genderCode": "M",
    "birthLocationCode": "75056"
  }
}
```

### 2. Erreur de validation (format de date incorrect)
**Requête** :
```json
{
  "lastName": "DUPONT",
  "firstNames": ["Jean", "Pierre"],
  "birthDate": "01-01-1990",  // Format incorrect
  "genderCode": "M",
  "birthLocationCode": "75056"
}
```

**Réponse** :
```json
{
  "timestamp": "2024-02-27T14:45:00.593+00:00",
  "status": 400,
  "error": "Le format de date est invalide. Format attendu : aaaa-MM-jj",
  "code": "E04_U010_KO_FORMAT_DATE",
  "path": "/v1/identities"
}
```

---

## Ressources supplémentaires

- **Swagger** : [PSI_swagger_v0.4.0_01-12-2025.yml](PSI_swagger_v0.4.0_01-12-2025.yml)
- **Schéma de la logique métier** : [PSI_Enrolement_par_un_tiers_(Logigramme).jpg](PSI_Enrolement_par_un_tiers_(Logigramme).jpg)
- **Postman Collection** : [Collection POSTMAN PSI](https://www.postman.com/red-rocket-401896/workspace/ans-prosanteconnect/collection/28025856-82d54247-aaae-487c-a421-f24127f1b8fd?action=share&source=copy-link&creator=28025856&active-environment=4825be29-6bab-4427-a868-28d0aca2433d).
- **Contenu du bouchon RPPS** : [Personnes_RPPS.csv](Personnes_RPPS.csv).

---

Si vous avez des questions ou des problèmes, veuillez contacter l'équipe technique à l'adresse suivante : **prosanteconnect.editeurs@esante.gouv.fr**.

--- 

