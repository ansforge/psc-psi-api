/*
 * Copyright © 2022-2024 Agence du Numérique en Santé (ANS) (https://esante.gouv.fr)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.ans.psc.psi.model;

import fr.ans.psc.model.Ps;

import java.util.ArrayList;
import java.util.List;

public class PsiPsAdapter extends Ps {

    private final int DEFAULT_QUALITY = 1;

    /*public PsiPsAdapter(User user) {
        ContactInfo contactInfo = new AmarContactInfoAdapter(ps);
        CivilStatus civilStatus = new AmarCivilStatusAdapter(ps);
        List<Practice> practices = new ArrayList<>();
        ps.getProfessions().forEach(profession -> practices.add(new AmarPracticeAdapter(profession)));
        List<AlternativeIdentifier> alternativeIdentifiers = new ArrayList<>();
        ps.getIds().forEach(id ->
        {
            AlternativeIdentifier identifier = new AlternativeIdentifier();
            identifier.setIdentifier(id);
            identifier.setOrigine(getOriginFromId(id));
            identifier.setQuality(DEFAULT_QUALITY);
            alternativeIdentifiers.add(identifier);
        });

        setNationalId(AttributeEncoding.encodeStringAttribute(ps.getNationalId()));
        setContactInfo(contactInfo);
        setCivilStatus(civilStatus);
        setPractices(practices);
        setAlternativeIdentifiers(alternativeIdentifiers);
    }*/
}