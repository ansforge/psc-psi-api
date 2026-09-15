# Exemples d'utilisation

Dans la version 0.9.0 de l'API PSI, lorsque l'API PSI fait appel au RNIPP, la réponse du RNIPP est transmise dans la réponse dans le bloc `rnippData`, que le compte ait été créé ou pas. Cette information permet à l'appelant de connaitre la ou les raisons du refus de creer le compte PSI lorsque les données sont refusées par l'INSEE.

## 1. Inscription simple

A ce jour l'insciption simple n'est plus acceptée. l'**ES** doit obligatoirement définir de type d'emploi de la personne enregistrée en renseignant le bloc `employment` (voir exemple 2).

### Requête

```bash
curl -X POST "https://psi-partenaire.gateway.api.esante.gouv.fr/v1/identities" \
  -H "Content-Type: application/json" \
  -H "ESANTE-API-KEY: <votre_api_key>" \
  -d '{
    "lastName": "DUPONT",
    "firstNames": "Jean Pierre",
    "birthDate": "1995-02-15",
    "genderCode": "M",
    "frenchNationality": true,
    "birthLocationCode": "33063",
    "professionalLastName": "Durant",
    "email": "jean.dupont@example.com",
    "phone": "+33612345678",
    "identifier": "8100112345678"
  }'
```

### Réponse `201`

```json
{
  "status": 201,
  "identity": {
    "status": 201,
    "id": "550e8400-e29b-41d4-a716-446655440001",
    "rppsIdentifiers": [
      "810000000000",
      "810000000111"
    ],
    "identityTraits": {
      "lastName": "DUPONT",
      "firstNames": "Jean Pierre",
      "birthDate": "1995-02-15",
      "genderCode": "M",
      "birthLocationCode": "33063",
      "professionalLastName": "Durant"
    },
    "message": "Le compte PSI a été créé avec succès."
  }
}
```

---

## 2. Inscription avec Employment

L'établissement peut transmettre un bloc `employment` afin de déclarer l'emploi de la personne et l'établissement émetteur.

```json
{
  "lastName": "PAYETTE",
  "firstNames": "Thaïs Kayla Aliyah",
  "birthDate": "2005-02-10",
  "genderCode": "F",
  "frenchNationality": true,
  "birthLocationCode": "97416",
  "professionalLastName": "PAYETTE",
  "email": "thais.payette@example.com",
  "phone": "+33620002675",
  "identifier": "810009151500",
  "employment": {
    "function": "Professionnel Soin",
    "issuingEstablishment": "1123456789",
    "employmentStartDate": "2026-06-15",
    "employmentEndDate": "2027-06-15"
  }
}
```
### Réponse `409`
```json
{
    "timestamp": "2026-07-27T14:06:13.661477388+02:00",
    "status": 409,
    "identity": {
        "status": 409,
        "psiIdentifier": "019fa371-abc1-746b-83ca-ada823e4d0df",
        "rppsIdentifiers": [810009151500],
        "error": "Le compte PSI existe déjà.",
        "code": "E04_U010_KO_PSI_ACCOUNT_ALREADY_EXIST_AND_HAVE_ONE_RPPS_ID"
    }
}
```
---

## 3. Inscription avec activité

```json
{
  "lastName": "PAYETTE",
  "firstNames": "Thaïs Kayla Aliyah",
  "birthDate": "2005-02-10",
  "genderCode": "F",
  "frenchNationality": true,
  "birthLocationCode": "97416",
  "professionalLastName": "PAYETTE",
  "email": "thais.payette@example.com",
  "phone": "+33620002675",
  "identifier": "810009151500",
  "employment": {
    "function": "Professionnel Soin",
    "issuingEstablishment": "1123456789",
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
    "issuingEstablishment": "19197161460104",
    "rppsRank": "19197161460104"
  }
}
```

### Réponse `207`

```json
{
  "status": 207,
  "identity": {
    "status": 201,
    "id": "550e8400-e29b-41d4-a716-446655440002",
    "rppsIdentifiers": [
      "810009151500"
    ],
    "message": "Le compte PSI a été créé avec succès.",
    "rnippData": {
        "metadonnees": {
            "dateIdentification": null,
            "codeReponseIdentification": 2,
            "divergences": null,
            "statutDuNomIdentifie": "O"
        },
        "individu": {
            "identites": [
                {
                    "statut": "O",
                    "nom": "PAYETTE",
                    "prenoms": [
                        "Thaïs",
                        "Kayla",
                        "Aliyah"
                    ]
                }
            ],
            "sexe": "F",
            "naissance": {
                "numeroActe": null,
                "date": "2005-02-10",
                "lieu": {
                    "code": "97416",
                    "libelleCommune": null,
                    "libellePays": null
                }
            },
            "deces": null
        }
    }
},
  "activity": {
    "status": 201,
    "code": "E04_U010_ACTIVITY_CREATED",
    "message": "L'activité a été créée"
  }
}
```

---

## 4. Inscription avec commande de carte PSI

```json
{
  "lastName": "PAYETTE",
  "firstNames": "Thaïs Kayla Aliyah",
  "birthDate": "2005-02-10",
  "genderCode": "F",
  "frenchNationality": true,
  "birthLocationCode": "97416",
  "professionalLastName": "PAYETTE",
  "email": "thais.payette@example.com",
  "phone": "+33620002675",
  "identifier": "",
  "employment": {
    "function": "Professionnel Soin",
    "issuingEstablishment": "1123456789",
    "employmentStartDate": "2026-06-15",
    "employmentEndDate": "2027-06-15"
  },
  "cardOrderPsi": {
    "addressee": "DR PAYETTE Thaïs",
    "streetNumber": "10",
    "streetName": "RUE DE LA PAIX",
    "additionalAddress": "Bâtiment B",
    "postalCode": "75002",
    "city": "PARIS",
    "country": "FRANCE"
  }
}
```

### Réponse `207`

```json
{
  "status": 207,
  "identity": {
    "status": 201,
    "id": "550e8400-e29b-41d4-a716-446655440006",
    "rppsIdentifiers": [],
    "identityTraits": {
      "lastName": "PAYETTE",
      "firstNames": "Thaïs Kayla Aliyah",
      "birthDate": "2005-02-10",
      "genderCode": "F",
      "birthLocationCode": "97416",
      "birthPlace": "97416",
      "birthCountryCode": "99100"
   },
    "message": "Le compte PSI a été créé avec succès. Aucun numéro RPPS n'a été trouvé pour ces traits d'identité.",
    "rnippData": {
        "metadonnees": {
            "dateIdentification": null,
            "codeReponseIdentification": 2,
            "divergences": null,
            "statutDuNomIdentifie": "O"
        },
        "individu": {
            "identites": [
                {
                    "statut": "O",
                    "nom": "PAYETTE",
                    "prenoms": [
                        "Thaïs",
                        "Kayla",
                        "Aliyah"
                    ]
                }
            ],
            "sexe": "F",
            "naissance": {
                "numeroActe": null,
                "date": "2005-02-10",
                "lieu": {
                    "code": "97416",
                    "libelleCommune": null,
                    "libellePays": null
                }
            },
            "deces": null
        }
    }
  },
  "cardOrderPsi": {
    "status": 200,
    "code": "E04_U010_CARD_ORDER_SUCCESSFUL",
    "message": "La commande de carte a été effectuée avec succès",
    "addressee": "DR PAYETTE Thaïs",
    "shippingAddress": "10 RUE DE LA PAIX Bâtiment B 75002 PARIS FRANCE"
  }
}
```

---

## 5. Inscription avec commande de carte CPS

```json
{
  "lastName": "PAYETTE",
  "firstNames": "Thaïs Kayla Aliyah",
  "birthDate": "2005-02-10",
  "genderCode": "F",
  "frenchNationality": true,
  "birthLocationCode": "97416",
  "professionalLastName": "PAYETTE",
  "email": "thais.payette@example.com",
  "phone": "+33620002675",
  "identifier": "810009151500",
  "employment": {
    "function": "Professionnel Soin",
    "issuingEstablishment": "1123456789",
    "employmentStartDate": "2026-06-15",
    "employmentEndDate": "2027-06-15"
  },
  "cardOrderCps": {
    "professionCode": "15",
    "professionalCategoryCode": "C"
  }
}
```

### Réponse

```json
{
  "status": 207,
  "identity": {
    "status": 201,
    "id": "550e8400-e29b-41d4-a716-446655440007",
    "rppsIdentifiers": [
      "810009151500"
    ],
    "message": "Le compte PSI a été créé avec succès.",
     "rnippData": {
        "metadonnees": {
            "dateIdentification": null,
            "codeReponseIdentification": 2,
            "divergences": null,
            "statutDuNomIdentifie": "O"
        },
        "individu": {
            "identites": [
                {
                    "statut": "O",
                    "nom": "PAYETTE",
                    "prenoms": [
                        "Thaïs",
                        "Kayla",
                        "Aliyah"
                    ]
                }
            ],
            "sexe": "F",
            "naissance": {
                "numeroActe": null,
                "date": "2005-02-10",
                "lieu": {
                    "code": "97416",
                    "libelleCommune": null,
                    "libellePays": null
                }
            },
            "deces": null
        }
    }
  },
  "cardOrderCps": {
    "status": 201,
    "code": "E04_U010_CARD_ORDER_SUCCESSFUL",
    "message": "La commande de carte a été effectuée avec succès"
  }
}
```

---

## 6. Inscription avec activité et commande de carte

Les traitements peuvent être combinés dans une même requête.

```json
{
  "lastName": "PAYETTE",
  "firstNames": "Thaïs Kayla Aliyah",
  "birthDate": "2005-02-10",
  "genderCode": "F",
  "frenchNationality": true,
  "birthLocationCode": "97416",
  "professionalLastName": "PAYETTE",
  "email": "thais.payette@example.com",
  "phone": "+33620002675",
  "identifier": "810009151500",

  "employment": {
    "function": "Professionnel Soin",
    "issuingEstablishment": "498765432109876",
    "employmentStartDate": "2026-02-15",
    "employmentEndDate": "2027-02-15"
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
    "issuingEstablishment": "19197161460104",
    "rppsRank": "19197161460104"
  },

  "cardOrderCps": {
    "professionCode": "15",
    "professionalCategoryCode": "C"
  }
}
```

La réponse est de type `207` et contient un bloc de résultat pour chaque traitement exécuté.

---

## 7. Professionnel étranger

Pour un professionnel de santé de nationalité étrangère, le `birthLocationCode` correspond au code INSEE du pays de naissance et `birthPlace` doit contenir le libellé de la ville étrangère.

Exemple issu du Swagger :

```json
{
  "lastName": "D'HONDT",
  "firstNames": "Marie-Louise Godelieve",
  "birthDate": "1999-07-05",
  "genderCode": "F",
  "frenchNationality": false,
  "birthLocationCode": "99131",
  "birthPlace": "Ottignies-Louvain-la-Neuve",
  "email": "marie-louise.dhondt@example.com",
  "phone": "+4740612345",
  "identifier": "538052778800000/1000",
  "employment": {
    "function": "Gestionnaire d'identité numérique",
    "issuingEstablishment": "412345678901234",
    "employmentStartDate": "2026-06-05",
    "employmentEndDate": "2027-06-05"
  }
}
```

Pour un professionnel étranger non trouvé au RNIPP, l'identité peut retourner un statut `404`, tandis qu'une activité éventuellement demandée peut disposer de son propre résultat dans une réponse `207`.

---

