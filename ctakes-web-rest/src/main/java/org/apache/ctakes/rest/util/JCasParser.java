/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.ctakes.rest.util;

import org.apache.ctakes.drugner.type.*;
import org.apache.ctakes.rest.service.CuiResponse;
import org.apache.ctakes.typesystem.type.textsem.*;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tmill on 12/20/18.
 */
public class JCasParser {
    List<Class<? extends Annotation>> semClasses = new ArrayList<>();

    public JCasParser(){
        // CUI types:
        semClasses.add(DiseaseDisorderMention.class);
        semClasses.add(SignSymptomMention.class);
        semClasses.add(ProcedureMention.class);
        semClasses.add(AnatomicalSiteMention.class);
        semClasses.add(MedicationMention.class);

        // Temporal types:
        semClasses.add(TimeMention.class);
        semClasses.add(DateAnnotation.class);

        // Drug-related types:
        semClasses.add(FractionStrengthAnnotation.class);
        semClasses.add(DrugChangeStatusAnnotation.class);
        semClasses.add(StrengthUnitAnnotation.class);
        semClasses.add(StrengthAnnotation.class);
        semClasses.add(RouteAnnotation.class);
        semClasses.add(FrequencyUnitAnnotation.class);
        semClasses.add(MeasurementAnnotation.class);
    }

    public Map<String, List<CuiResponse>> parse(JCas jcas) throws Exception {

        Map<String, List<CuiResponse>> responseMap = new HashMap<>();
        for(Class<? extends Annotation> semClass : semClasses){
            List<CuiResponse> annotations = new ArrayList<>();
            for(Annotation annot : JCasUtil.select(jcas, semClass)){
                CuiResponse response = new CuiResponse(annot);
                annotations.add(response);
            }
            responseMap.put(semClass.getSimpleName(), annotations);
        }
        return responseMap;
    }
}
