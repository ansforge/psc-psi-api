# Documentation de l'API PSI

> **Version de l'API : v0.9.0**  
> **OpenAPI : 3.0.3**  
> **Dernière définition Swagger : 07/09/2026**

L'API **Pro Santé Identité (PSI)** permet aux acteurs habilités d'interagir avec le service PSI pour :

- **inscrire un professionnel de santé** dans Pro Santé Identité ;
- **obtenir le ou les identifiants RPPS** associés à une identité ;
- **créer ou modifier une activité** d'un professionnel de santé dans le RPPS ;
- **déclarer un emploi** permettant notamment d'identifier l'établissement à l'origine de l'enregistrement de la personne ;
- **commander une carte PSI** ;
- **commander une carte CPS**.

Cette version regroupe donc plusieurs opérations dans une même requête `POST /v1/identities`. Les blocs `activity`, `cardOrderPsi` et `cardOrderCps` sont optionnels.

---

## Table des matières

1. [Introduction](#introduction)
2. [Prérequis](#prérequis)
3. [Vue d'ensemble du fonctionnement](#vue-densemble-du-fonctionnement)
4. [Spécifications techniques](#spécifications-techniques)
   1. [Authentification](#authentification)
   2. [Endpoint](#endpoint)
   3. [Structure générale de la requête](#structure-générale-de-la-requête)
   4. [Données d'identité](#données-didentité)
   5. [Bloc Employment](#bloc-employment)
   6. [Bloc Activity](#bloc-activity)
   7. [Bloc Card Order PSI](#bloc-card-order-psi)
   8. [Bloc Card Order CPS](#bloc-card-order-cps)
5. [Codes de réponse HTTP](#codes-de-réponse-http)
6. [Gestion des réponses multi-opérations - HTTP 207](#gestion-des-réponses-multi-opérations---http-207)
7. [Codes métier](#codes-métier)
   1. [Identité](#codes-métier-identité)
   2. [Activité](#codes-métier-activité)
   3. [Commande de carte](#codes-métier-commande-de-carte)
8. [Exemples d'utilisation](#exemples-dutilisation)
   1. [Inscription simple](#1-inscription-simple)
   2. [Inscription avec Employment](#2-inscription-avec-employment)
   3. [Inscription avec activité](#3-inscription-avec-activité)
   4. [Inscription avec commande de carte PSI](#4-inscription-avec-commande-de-carte-psi)
   5. [Inscription avec commande de carte CPS](#5-inscription-avec-commande-de-carte-cps)
   6. [Inscription avec activité et commande de carte](#6-inscription-avec-activité-et-commande-de-carte)
   7. [Professionnel étranger](#7-professionnel-étranger)
9. [Erreurs et cas particuliers](#erreurs-et-cas-particuliers)
10. [Ressources supplémentaires](#ressources-supplémentaires)

---

## Introduction

L'API PSI est une API REST permettant à des appelants autorisés, notamment des **établissements de santé (ES)**, d'enregistrer des professionnels de santé dans Pro Santé Identité.

L'enregistrement s'appuie sur les traits d'identité transmis par l'appelant et sur le rapprochement avec le **RNIPP**. Selon le contexte, l'appel peut également déclencher :

- la création ou la modification d'un **emploi**;
- la création ou la modification d'une **activité** (pour les PS RPPS uniquement);
- une **commande de carte PSI** ;
- une **commande de carte CPS**.

L'API est exposée via **Gravitee** de l'ANS à l'adresse :

**https://psi-partenaire.gateway.api.esante.gouv.fr**

> La présente documentation est dérivée du Swagger `PSI_swagger_activity_card_v0.9.0_(2026-09-07).yml`, version `v0.9.0`.

---

## Prérequis

Avant d'utiliser l'API, l'appelant doit disposer des éléments suivants :

- un accès autorisé à l'API via Gravitee ;
- une **clé API** ;
- les droits permettant d'utiliser le service dans le contexte de l'appelant ;
- un outil de test d'API REST tel que **Postman** ou `curl`.

### Authentification

Les appels sont authentifiés par une clé API transmise dans l'en-tête :

```http
ESANTE-API-KEY: <votre_api_key>
```

L'en-tête `Content-Type` doit être positionné à :

```http
Content-Type: application/json
```

---

# Vue d'ensemble du fonctionnement

L'endpoint `/v1/identities` permet de combiner plusieurs traitements.

### Traitement de l'identité

L'API :

1. reçoit les traits d'identité ;
2. recherche et rapproche la personne auprès du RNIPP ;
3. recherche les informations RPPS associées lorsque cela est possible ;
4. crée le compte PSI lorsque les conditions sont réunies ;
5. retourne l'identifiant PSI et le ou les identifiants RPPS associés.

### Traitement des opérations complémentaires

Les blocs suivants peuvent être ajoutés à la même requête :

| Bloc | Fonction |
|---|---|
| `employment` | Déclare ou modifie un emploi et identifie l'établissement à l'origine de l'enregistrement |
| `activity` | Crée ou modifie une activité (pour les PS RPPS uniquement) |
| `cardOrderPsi` | Demande une carte PSI |
| `cardOrderCps` | Demande une carte CPS |

Une requête peut donc, par exemple, inscrire une personne, déclarer son emploi, créer son activité (pour un PS RPPS) et commander une carte CPS.

Lorsque plusieurs traitements sont exécutés dans le cadre d'une même requête, l'API peut retourner **HTTP 207 Multi-Status**. Le statut global `207` ne signifie pas que toutes les opérations ont nécessairement réussi : il faut examiner le statut et le code métier de chaque bloc de réponse.

### Logique métier

Pour des raisons de lisibilité, le schéma de fonctionnement est decomposé en 4 schémas
Un **schéma chapeau** et les sous-schémas **A, B, C et D**

Pour faciliter la lecture des schémas :
- **PSI** : Pro Santé Identité
- **RNIPP** : Répertoire national d'identification des personnes physiques
- **RPPS** : Répertoire partagé des Professionnels intervenant dnas le systeème de santé
- **SEC-PSC** : Remplacer "SEC-PSC" par "API PSC" : API permettant d'interroger le réferentiel PSC contenant les CPE, CPA, les RPPS à J+1 et les MIE

### Schémas de la logique métier
 [[E04_Chapeau.jpg](E04_Chapeau.jpg)]
 [[E04_A_Traitements_communs.jpg](E04_A_Traitements_communs.jpg)]
 [[E04_B.jpg](E04_B.jpg)]
 [[E04_C.jpg](E04_C.jpg)]
 [[E04_D.jpg](E04_D.jpg)]

---

# Spécifications techniques

## Authentification

| Élément | Valeur |
|---|---|
| Type | API Key |
| Nom de l'en-tête | `ESANTE-API-KEY` |
| Emplacement | Header |

Exemple :

```http
ESANTE-API-KEY: xxxxxxxxxxxxxxxxx
```

---

## Endpoint

### Créer une identité PSI

```http
POST /v1/identities
```

URL complète :

```text
https://psi-partenaire.gateway.api.esante.gouv.fr/v1/identities
```

### Headers

```http
Content-Type: application/json
ESANTE-API-KEY: <votre_api_key>
```

---

## Structure générale de la requête

La structure générale est la suivante :

```json
{
  "lastName": "DUPONT",
  "firstNames": "Jean Pierre",
  "birthDate": "1995-02-15",
  "genderCode": "M",
  "frenchNationality": true,
  "birthLocationCode": "33063",
  "birthPlace": "Bordeaux",
  "professionalLastName": "Durant",
  "email": "jean.dupont@example.com",
  "phone": "+33612345678",
  "identifier": "8100112345678",

  "employment": {
    "function": "Professionnel Soin",
    "issuingEstablishment": "123456789",
    "employmentStartDate": "2026-06-15",
    "employmentEndDate": "2027-06-15"
  },

  "activity": {
    "professionCode": "15",
    "professionalCategoryCode": "C",
    "activityStartDate": "2026-06-15",
    "activityEndDate": "2027-06-30",
    "function": "FON-01",
    "activityGenre": "GENR01",
    "practiceMode": "L",
    "liberalActivityTypeCode": "ACT-LIB-01",
    "activityEndReason": "AUT",
    "issuingEstablishment": "123456789",
    "numFINESSET": "123456789",
    "numSIRET": "",
    "rppsRank": ""
  },

  "cardOrderPsi": {
    "addressee": "DR DUPONT Jean",
    "streetNumber": "10",
    "streetName": "RUE DE LA PAIX",
    "additionalAddress": "Bâtiment B",
    "postalCode": "75002",
    "city": "PARIS",
    "country": "FRANCE"
  },

  "cardOrderCps": {
    "professionCode": "15",
    "professionalCategoryCode": "C"
  }
}
```

Les quatre blocs complémentaires sont indépendants dans leur structure :

- `employment` : obligatoire ;
- `activity` : facultatif ;
- `cardOrderPsi` : facultatif ;
- `cardOrderCps` : facultatif.

Attention les blocs cardOrderPsi et cardOrderCps sont exclusifs, seul l'un des deux peut être utilisé selon qu'il s'agit d'un PS RPPS (renseigner cardOrderCps) ou non (renseigner cardOrderPsi)

---

# Données d'identité

## Champs principaux

| Champ | Type | Obligatoire | Description |
|---|---|---:|---|
| `lastName` | string | **Oui** | Nom de famille de l'état civil tel que trouvé au RNIPP |
| `firstNames` | string | **Oui** | Prénom(s) de l'état civil |
| `birthDate` | string | **Oui** | Date de naissance au format `AAAA-MM-JJ` |
| `genderCode` | string | **Oui** | `M`, `F` ou `I` |
| `birthLocationCode` | string | **Oui** | Code INSEE de la commune ou du pays de naissance |
| `frenchNationality` | boolean | Non | Indique si la personne possède la nationalité française |
| `birthPlace` | string | Non | Libellé de la ville étrangère ; obligatoire lorsque `birthLocationCode` correspond à un pays étranger |
| `professionalLastName` | string | Non | Nom de famille professionnel |
| `email` | string | Non* | Adresse e-mail |
| `phone` | string | Non* | Numéro de téléphone avec indicatif |
| `identifier` | string | Non* | Identifiant de l'appelant ou du professionnel |

### Obligations dépendant de l'appelant

D'après le Swagger :

- `email` et `phone` sont obligatoires ;
- pour un professionnel de santé **non français**, `identifier` est obligatoire et peut être de type RPPS, ou un identifiant local (comme pour les CPE, CPA) ;
- si `cardOrderCps` est présent, `identifier` doit être de type RPPS.

### `genderCode`

| Valeur | Signification |
|---|---|
| `M` | Masculin |
| `F` | Féminin |
| `I` | Inconnu |

### `birthLocationCode`

Le champ comporte exactement **5 caractères**.

- Pour une naissance en France : code INSEE de la commune.
- Pour une naissance à l'étranger : code INSEE du pays de naissance.

Lorsque la naissance est à l'étranger, `birthPlace` doit également être renseigné avec le libellé de la ville étrangère.

### `phone`

Le format attendu est :

```text
^+?[0-9]{4,15}$
```

Exemples :

```text
0612345678
+330612345678
+33612345678
```

Lorsqu'un numéro est fourni sans indicatif, l'indicatif `+33` est appliqué par défaut.

---

# Bloc Employment

Le bloc `employment` permet de déclarer ou de modifier un **emploi**.

Il sert notamment à associer une personne enregistrée à l'**établissement à l'origine de l'appel**. Cette action est indispensable pour légitimer la présence d'une personne s'etant auto-enrôlée via l'IHM, et ainsi valider son compte PSI. Sans validation du compte PSI par un **ES**, le compte PSI est supprimé au bout de 30 jours calendaires.

## Structure

```json
"employment": {
  "function": "Professionnel Soin",
  "issuingEstablishment": "123456789",
  "employmentStartDate": "2026-06-15",
  "employmentEndDate": "2027-06-15"
}
```

## Champs

| Champ | Type | Obligatoire | Description |
|---|---|---:|---|
| `function` | string | **Oui** | Type du poste |
| `issuingEstablishment` | string | **Oui** | Identifiant FINESS ou SIRET de l'établissement émetteur de l'appel |
| `employmentStartDate` | string | Non | Date de début d'emploi |
| `employmentEndDate` | string | Non | Date de fin d'emploi |

Les dates sont au format :

```text
AAAA-MM-JJ
```

La date de fin doit être supérieure ou égale à la date de début lorsqu'elle est renseignée.

### Valeurs de `function`

Le Swagger définit actuellement les postes suivants :

- `Responsable légal ou personne habilitée à engager la structure`
- `Mandataire`
- `Gestionnaire d'identité numérique`
- `Professionnel Achat-Logistique`
- `Professionnel Gestion de l'information`
- `Professionnel Ingénierie et maintenance technique`
- `Professionnel Management, gestion et aide à la décision`
- `Professionnel Qualité, hygiène, sécurité et environnement`
- `Professionnel Recherche clinique`
- `Professionnel Social, éducatif, psychologique et culturel`
- `Professionnel Soin` **<== qui est la seule valeur acceptée à ce jour**
- `Professionnel Systèmes d'information`
- `Autorité de certification d'identité EDC PSC`

### `issuingEstablishment`

L'identifiant de l'établissement émetteur doit correspondre à la structure utilisée pour l'appel.

Le Swagger indique qu'il s'agit d'un identifiant d'établissement sur **9 ou 14 chiffres**, selon le type d'identifiant utilisé.

---

# Bloc Activity

Le bloc `activity` permet de **créer ou modifier une activité**. Ce bloc est réservé aux PS RPPS, RPPS+, eRPPS.

## Structure

```json
"activity": {
  "professionCode": "15",
  "professionalCategoryCode": "C",
  "activityStartDate": "2026-06-15",
  "activityEndDate": "2027-06-30",
  "function": "FON-01",
  "activityGenre": "GENR01",
  "practiceMode": "L",
  "liberalActivityTypeCode": "ACT-LIB-01",
  "activityEndReason": "AUT",
  "issuingEstablishment": "19197161460104",
  "numFINESSET": "123456789"
}
```

## Champs

| Champ | Type | Obligatoire | Description |
|---|---|---:|---|
| `professionCode` | string | **Oui** | Code profession sur 2 chiffres |
| `professionalCategoryCode` | string | **Oui** | Catégorie professionnelle |
| `activityStartDate` | string | **Oui** | Date de début d'activité |
| `activityEndDate` | string | Non | Date de fin d'activité |
| `function` | string | **Oui** | Code fonction |
| `activityGenre` | string | **Oui** | Genre d'activité |
| `practiceMode` | string | **Oui** | Mode d'exercice |
| `liberalActivityTypeCode` | string | Non | Type d'activité libérale |
| `activityEndReason` | string | Non | Motif de fin d'activité |
| `issuingEstablishment` | string | **Oui** | Établissement émetteur de l'appel |
| `numFINESSET` | string | Non | FINESS de l'établissement géographique |
| `numSIRET` | string | Non | SIRET de l'établissement géographique |
| `rppsRank` | string | Non | RPPS RANG de l'établissement géographique |

### `professionCode`

Code profession sur **2 chiffres**.

Le Swagger renvoie vers la nomenclature :

`MOS - JDV_J106-EnsembleProfession-RASS`

### `professionalCategoryCode`

| Valeur | Signification |
|---|---|
| `C` | Civil |
| `E` | Étudiant |
| `M` | Agent de l'État |

### `practiceMode`

| Valeur | Signification |
|---|---|
| `L` | Libéral |
| `S` | Salarié |
| `B` | Bénévole |

Le Swagger renvoie vers `JDV_J117-ModeExercice-ENREG`.

### `function`

Le format attendu est :

```text
3 chiffres
```

ou :

```text
FON-xx
```

où `xx` correspond à deux chiffres.

Le Swagger renvoie vers `MOS - JDV_J121-RolePriseCharge-ENREG`.

### `activityGenre`

Le format est :

```text
GENRxx
```

Le Swagger cite notamment :

| Code | Signification |
|---|---|
| `GENR01` | Activité de soin et de pharmacie |
| `GENR08` | Coordination et orientation |
| `GENR09` | Administratif ou appui à l'organisation de l'accompagnement social/médico-social |
| `GENR10` | Accompagnement social/médico-social à la vie sociale, professionnelle, éducative |
| `GENR11` | Accompagnement social/médico-social au soin |
| `GENR12` | Encadrement et organisation de l'accompagnement |
| `GENR13` | Médico-administratif |
| `GENR99` | Autre activité |

Le jeu complet de valeurs est défini dans `MOS - JDV_J116-GenreActivite-ENREG`.

### `liberalActivityTypeCode`

Format :

```text
ACT-LIB-xx
```

Valeurs documentées :

| Code | Signification |
|---|---|
| `ACT-LIB-01` | Cabinet primaire |
| `ACT-LIB-02` | Cabinet secondaire |
| `ACT-LIB-03` | Plateau technique |
| `ACT-LIB-04` | Secteur privé à l'hôpital |
| `ACT-LIB-05` | Autre lieu d'exercice ou autre site |
| `ACT-LIB-06` | Cabinet |

Le Swagger renvoie vers `MOS - JDV_J119-TypeActiviteLiberale-ENREG`.

### `activityEndReason`

Valeurs documentées :

| Code | Signification |
|---|---|
| `AUT` | Autre motif |
| `CHA` | Changement d'activité |
| `CHL` | Changement de lieu d'exercice |
| `CHP` | Changement de profession |
| `DCD` | Décès |
| `ETR` | Départ à l'étranger |
| `RH` | Retraite hospitalière |
| `RL` | Retraite libérale |
| `RS` | Retraite salariée |

Le Swagger précise que le jeu complet est défini dans la NOS de l'ANS.

### Établissement géographique

Les trois champs suivants sont mutuellement exclusifs :

- `numFINESSET`
- `numSIRET`
- `rppsRank`

Si l'un d'eux est renseigné, les deux autres ne doivent pas l'être:
- Soit donner aux attributs non valorisés la valeur `null`
- Soit ne pas envoyer les attributs non valorisés dans la requête.

| Champ | Format |
|---|---|
| `numFINESSET` | 9 chiffres |
| `numSIRET` | 14 chiffres |
| `rppsRank` | 14 chiffres, commençant par `1` |

---

# Bloc Card Order PSI

Le bloc `cardOrderPsi` permet de déclencher une **commande de carte PSI**.

## Structure

```json
"cardOrderPsi": {
  "addressee": "DR DUPONT Jean",
  "streetNumber": "10",
  "streetName": "RUE DE LA PAIX",
  "additionalAddress": "Bâtiment B",
  "postalCode": "75002",
  "city": "PARIS",
  "country": "FRANCE"
}
```

Une seule commande de type carte est attendue dans une requête : `cardOrderPsi` ou `cardOrderCps`.

## Champs

| Champ | Description |
|---|---|
| `addressee` | Destinataire |
| `streetNumber` | Numéro dans la voie |
| `streetName` | Nom de la voie |
| `additionalAddress` | Complément d'adresse |
| `postalCode` | Code postal |
| `city` | Ville |
| `country` | Pays |

### Adresse en France

Lorsque `country` est absent ou vaut `FRANCE`, la validité de l'adresse est contrôlée via l'API de géocodage **IGN/BAN**.

### Adresse à l'étranger

Lorsque `country` est renseigné avec une valeur différente de `FRANCE`, les champs suivants sont au minimum requis :

- `addressee`
- `streetName`
- `city`
- `postalCode`
- `country`

---

# Bloc Card Order CPS

Le bloc `cardOrderCps` permet de déclencher une **commande de carte CPS**.
L'adresse de livraison est récupérée depuis le RPPS, l'API PSI ne permet pas de modifier l'adresse de livraison pour la commande de carte CPS.

## Structure

```json
"cardOrderCps": {
  "professionCode": "15",
  "professionalCategoryCode": "C"
}
```

Les deux champs sont obligatoires.

| Champ | Type | Obligatoire | Description |
|---|---|---:|---|
| `professionCode` | string | **Oui** | Code profession sur 2 chiffres |
| `professionalCategoryCode` | string | **Oui** | `C`, `E` ou `M` |

Le Swagger précise que les valeurs doivent être valides pour permettre la commande.

Lorsque `cardOrderCps` est présent, l'`identifier` transmis dans la requête doit être de type **RPPS**.

---

# Codes de réponse HTTP

| HTTP | Signification | Cas principal |
|---:|---|---|
| `201` | Created | Identité créée avec succès |
| `207` | Multi-Status | Plusieurs traitements ont été exécutés et leurs résultats sont retournés séparément |
| `400` | Bad Request | Données invalides, erreur de validation, divergence RNIPP ou échec de traitement lié |
| `404` | Not Found | Personne non trouvée au RNIPP |
| `409` | Conflict | Compte PSI déjà existant |
| `500` | Internal Server Error | Erreur interne |
| `503` | Service Unavailable | Service temporairement indisponible |

> **Important :** avec une requête contenant plusieurs blocs fonctionnels, le statut HTTP global `207` doit être interprété avec les statuts propres à `identity`, `activity`, `cardOrderPsi` et `cardOrderCps`.

---

# Gestion des réponses multi-opérations - HTTP 207

Le statut `207 Multi-Status` est utilisé lorsque plusieurs services ou traitements interviennent.

Exemple :

```json
{
  "status": 207,
  "identity": {
    "status": 201,
    "id": "550e8400-e29b-41d4-a716-446655440002",
    "rppsIdentifiers": [
      "810000000000"
    ],
    "message": "Le compte PSI a été créé avec succès."
  },
  "activity": {
    "status": 201,
    "code": "E04_U010_ACTIVITY_CREATED",
    "message": "L'activité a été créée"
  },
  "cardOrderCps": {
    "status": 201,
    "code": "E04_U010_CARD_ORDER_SUCCESSFUL",
    "message": "La commande de carte a été effectuée avec succès"
  }
}
```

Dans cet exemple :

- l'identité a été créée (`201`) ;
- l'activité a été créée (`201`) ;
- la commande de carte CPS a réussi (`201`) ;
- le statut global est `207` car plusieurs traitements ont été exécutés.

### Exemple de traitement partiellement en échec

```json
{
  "status": 207,
  "identity": {
    "status": 201,
    "message": "Le compte PSI a été créé avec succès."
  },
  "activity": {
    "status": 400,
    "code": "E04_U010_ACTIVITY_FAILED",
    "message": "La création de l'activité a échoué"
  },
  "cardOrderPsi": {
    "status": 424,
    "code": "E04_U010_CARD_ORDER_FAILED_DEPENDENCY",
    "message": "La commande de carte n'a pas pu être traitée car une opération préalable a échoué."
  }
}
```

L'identité est créée alors que les traitements suivants peuvent échouer.

---

# Codes métier

## Codes métier identité

Les codes d'identité renvoyés par l'API comprennent notamment :

| Code | Signification |
|---|---|
| `E04_U010_KO_RNIPP_FOUND_WITH_DIVERGENCES` | Le RNIPP a trouvé une personne mais les traits fournis divergent |
| `E04_U010_KO_RNIPP_SYNTAX_ERROR` | Le RNIPP a rejeté la demande pour une erreur de syntaxe |
| `E04_U010_KO_RNIPP_NOT_FOUND` | La personne n'existe pas au RNIPP |
| `E04_U010_KO_PSI_ACCOUNT_ALREADY_EXIST_AND_HAVE_ONE_RPPS_ID` | Le compte PSI existe déjà |
| `E04_U010_KO_IDENTITY_MISMATCH_WITH_ID_INPUT` | L'identifiant fourni ne correspond pas aux traits d'identité transmis |
| `E04_U010_KO_FORMAT_DATE` | Format de date invalide |

Le Swagger contient également des codes de validation plus spécifiques, notamment pour les champs obligatoires, le sexe, l'e-mail, le téléphone, l'adresse, le lieu de naissance et l'identifiant.

---

## Codes métier activité

| Code | Signification |
|---|---|
| `E04_U010_ACTIVITY_CREATED` | Activité créée |
| `E04_U010_ACTIVITY_UPDATED` | Activité modifiée |
| `E04_U010_ACTIVITY_FAILED_DEPENDENCY` | L'activité n'a pas pu être créée/modifiée car une opération préalable a échoué |
| `E04_U010_ACTIVITY_SERVICE_UNAVAILABLE` | Service activité indisponible |
| `E04_U010_ACTIVITY_BAD_REQUEST` | Requête activité invalide |

---

## Codes métier commande de carte

| Code | Signification |
|---|---|
| `E04_U010_CARD_ORDER_SUCCESSFUL` | Commande de carte effectuée avec succès |
| `E04_U010_CARD_ORDER_ADDRESS_REFUSED` | Commande refusée en raison de l'adresse |
| `E04_U010_CARD_ORDER_FAILED_DEPENDENCY` | Commande impossible en raison d'un échec d'une opération préalable |
| `E04_U010_CARD_ORDER_REFUSED` | Commande de carte refusée |
| `E04_U010_CARD_ORDER_REFUSED_CPS` | Commande de carte CPS refusée |
| `E04_U010_CARD_ORDER_SERVICE_UNAVAILABLE` | Service de commande de carte indisponible |

---

# Exemples d'utilisation

## 1. Inscription simple (obsolete)

A ce jour l'insciption simple n'est plus acceptée. l'**ES** doit obligatoirement définir de type d'emploi de la personne enregistrée en renseignant le bloc `employment`.

## 2. Inscription avec Employment

L'établissement peut transmettre un bloc `employment` afin de déclarer l'emploi de la personne et l'établissement émetteur. Tous les autres exemples contiennent ce bloc.

## 3. Inscription avec activité

L'établissement peut transmettre une activité qui concerne un PS RPPS (création/modification) grace au bloc `activity`

## 4. Inscription avec commande de carte PSI

## 5. Inscription avec commande de carte CPS

## 6. Inscription avec activité et commande de carte

Les traitements peuvent être combinés dans une même requête.

## 7. Professionnel étranger

Pour un professionnel de santé de nationalité étrangère, le `birthLocationCode` correspond au code INSEE du pays de naissance et `birthPlace` doit contenir le libellé de la ville étrangère.

Pour un professionnel étranger non trouvé au RNIPP, l'identité peut retourner un statut `404`, tandis qu'une activité éventuellement demandée peut disposer de son propre résultat dans une réponse `207`.

---

# Erreurs et cas particuliers

## Erreur de validation - HTTP 400

Exemple : date de naissance invalide.

```json
{
  "timestamp": "2024-02-27T14:45:00.593000+00:00",
  "status": 400,
  "identity": {
    "status": 400,
    "error": "Des erreurs ont été détectées lors de la validation. Veuillez consulter le détail",
    "code": "E04_U010_KO_FORMAT_DATE",
    "detail": [
      {
        "object": "registerIdentityRequestDto",
        "field": "birthDate",
        "message": "Le format de date est invalide. Format attendu : aaaa-MM-jj",
        "rejected": "01-01-2000",
        "constraint": "Pattern"
      }
    ]
  },
  "path": "/v1/identities"
}
```

## Divergence RNIPP - HTTP 400

Si le RNIPP trouve une personne mais que les traits transmis divergent :

```json
{
  "status": 400,
  "identity": {
    "status": 400,
    "error": "Une identité a été trouvée au RNIPP mais elle diverge des traits d'identité fournis",
    "code": "E04_U010_KO_RNIPP_FOUND_WITH_DIVERGENCES"
  },
  "path": "/v1/identities"
}
```

Les informations RNIPP peuvent également contenir le détail des divergences :

```json
"divergences": {
  "nom": true,
  "prenoms": false,
  "sexe": false,
  "dateNaissance": false,
  "lieuNaissance": false
}
```

## Personne non trouvée au RNIPP - HTTP 404

```json
{
  "timestamp": "2024-02-27T14:45:00.593000+00:00",
  "status": 404,
  "identity": {
    "status": 404,
    "rppsIdentifiers": [
      "810000000000"
    ],
    "error": "La personne n'existe pas au RNIPP, le compte PSI ne peut pas être créé",
    "code": "E04_U010_KO_RNIPP_NOT_FOUND"
  },
  "path": "/v1/identities"
}
```

## Compte PSI déjà existant - HTTP 409

```json
{
  "timestamp": "2025-09-17T12:40:04.8687343+02:00",
  "status": 409,
  "identity": {
    "status": 409,
    "psiIdentifier": "550e8400-e29b-41d4-a716-446655440008",
    "rppsIdentifiers": [
      "810000000000"
    ],
    "error": "Le compte PSI existe déjà.",
    "code": "E04_U010_KO_PSI_ACCOUNT_ALREADY_EXIST_AND_HAVE_ONE_RPPS_ID"
  },
  "path": "/v1/identities"
}
```

Lorsque le compte existe déjà, une requête comportant une activité ou une commande de carte peut toutefois retourner `207` avec le résultat propre à cette opération.

Exemple :

```json
{
  "status": 207,
  "identity": {
    "status": 409,
    "psiIdentifier": "550e8400-e29b-41d4-a716-446655440004",
    "rppsIdentifiers": [
      "810000000000"
    ],
    "error": "Le compte PSI existe déjà",
    "code": "E04_U010_KO_PSI_ACCOUNT_ALREADY_EXIST_AND_HAVE_ONE_RPPS_ID"
  },
  "activity": {
    "status": 200,
    "code": "E04_U010_ACTIVITY_UPDATED",
    "message": "L'activité a été modifiée"
  }
}
```

---

# Modèle de réponse identité

Lorsqu'une identité est créée, le bloc `identity` peut notamment contenir :

| Champ | Description |
|---|---|
| `status` | Statut HTTP du traitement identité |
| `id` | Identifiant UUID du compte PSI |
| `psiIdentifier` | Identifiant PSI lorsque le compte existe déjà |
| `rppsIdentifiers` | Liste des identifiants RPPS associés |
| `identityTraits` | Traits d'identité issus du rapprochement |
| `message` | Message fonctionnel |
| `rnippData` | Données retournées par le rapprochement RNIPP |
| `error` | Message d'erreur |
| `code` | Code métier |

---

# Modèle de réponse activité

Le bloc `activity` peut notamment contenir :

```json
{
  "status": 201,
  "code": "E04_U010_ACTIVITY_CREATED",
  "message": "L'activité a été créée"
}
```

ou, en cas de modification :

```json
{
  "status": 200,
  "code": "E04_U010_ACTIVITY_UPDATED",
  "message": "L'activité a été modifiée"
}
```

En cas d'erreur de validation, le bloc peut contenir une liste `erreurs` :

```json
{
  "status": 400,
  "code": "E04_U010_ACTIVITY_BAD_REQUEST",
  "erreurs": [
    {
      "message": "Le format de la date doit être 'AAAA-MM-DD'"
    },
    {
      "message": "paramètre obligatoire manquant codeProfession"
    }
  ]
}
```

---

# Modèle de réponse commande de carte PSI

Exemple de succès :

```json
{
  "status": 201,
  "code": "E04_U010_CARD_ORDER_SUCCESSFUL",
  "message": "La commande de carte a été effectuée avec succès",
  "addressee": "DR DUPONT Jean",
  "shippingAddress": "10 RUE DE LA PAIX Bâtiment B 75002 PARIS FRANCE"
}
```

La réponse peut notamment contenir une adresse corrigée ou normalisée par l'API.

---

# Modèle de réponse commande de carte CPS

Exemple de succès :

```json
{
  "status": 201,
  "code": "E04_U010_CARD_ORDER_SUCCESSFUL",
  "message": "La commande de carte a été effectuée avec succès"
}
```

---

# Ressources supplémentaires

- **Swagger / OpenAPI** : [[PSI_swagger_activity_card_v0.9.0_(2026-09-07).yml](PSI_swagger_activity_card_v0.9.0_(2026-09-07).yml)]
- **API** : `https://psi-partenaire.gateway.api.esante.gouv.fr`
- **Schéma de la logique métier** : 
- [[E04_Chapeau.jpg](E04_Chapeau.jpg)]
- [[E04_A.jpg](E04_A.jpg)]
- [[E04_B.jpg](E04_B.jpg)]
- [[E04_C.jpg](E04_C.jpg)]
- [[E04_D.jpg](E04_D.jpg)]
- **Collection Postman** : [Collection POSTMAN PSI](https://www.postman.com/red-rocket-401896/ans-prosanteconnect/collection/28025856-53c7-43c561-4d99-b1d7-83bcf61e82ca?action=share&source=copy-link&creator=28025856)

---

## Référence de la version

Cette documentation correspond à la définition OpenAPI :

```text
title   : PSI swagger 24-07-2026
version : v0.9.0
OpenAPI : 3.0.3
```

Serveur :

```text
https://psi-partenaire.gateway.api.esante.gouv.fr
```

Endpoint exposé dans cette version :

```text
POST /v1/identities
```

---

Pour toute question ou anomalie concernant l'intégration, contacter l'équipe technique PSI/Pro Santé Connect via les canaux habituels.
