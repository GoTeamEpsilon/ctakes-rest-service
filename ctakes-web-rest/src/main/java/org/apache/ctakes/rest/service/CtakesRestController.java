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
package org.apache.ctakes.rest.service;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.ctakes.core.pipeline.PipelineBuilder;
import org.apache.ctakes.core.pipeline.PiperFileReader;
import org.apache.ctakes.core.resource.FileLocator;
import org.apache.ctakes.rest.util.XMLParser;
import org.apache.log4j.Logger;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.cas.impl.XmiCasSerializer;
import org.apache.uima.jcas.JCas;
import org.apache.uima.util.JCasPool;
import org.springframework.web.bind.annotation.*;
import javax.annotation.PostConstruct;
import javax.servlet.ServletException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServlet;
import org.apache.ctakes.core.util.OntologyConceptUtil;
import org.apache.ctakes.typesystem.type.refsem.UmlsConcept;
import org.apache.ctakes.typesystem.type.relation.RelationArgument;
import org.apache.ctakes.typesystem.type.relation.ResultOfTextRelation;
import org.apache.ctakes.typesystem.type.textsem.LabMention;
import org.apache.ctakes.typesystem.type.textsem.MedicationMention;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.tcas.Annotation;
import org.json.simple.JSONObject;


/*
 * Rest web service that takes clinical text
 * as input and produces extracted text as output
 */
@RestController
public class CtakesRestController {

    private static final Logger LOGGER = Logger.getLogger(CtakesRestController.class);
    private static final String PIPER_FILE_PATH = "pipers/RaxaMentions.piper";
    private AnalysisEngine engine;
    private JCasPool pool;

    @PostConstruct
    public void init() throws ServletException {
        LOGGER.info("Initializing analysis engine and jcas pool");
        try {
            final File inputFile = FileLocator.getFile(PIPER_FILE_PATH);
            PiperFileReader reader = new PiperFileReader(inputFile.getAbsolutePath());
            PipelineBuilder builder = reader.getBuilder();
            AnalysisEngineDescription analysisEngineDesc = builder.getAnalysisEngineDesc();            
            engine = UIMAFramework.produceAnalysisEngine(analysisEngineDesc);
            pool = new JCasPool( 100, engine );
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
    }

    @RequestMapping(value="/analyze", method = RequestMethod.POST)
    @ResponseBody
    public Map<String,List<String>> getAnalyzedJSON(@RequestBody String analysisText)
            throws ServletException, IOException {
        Map<String,List<String>>  resultMap = null;
        if (analysisText != null && analysisText.trim().length() > 0) {
            try {
                JCas jcas = pool.getJCas(-1);
                jcas.setDocumentText(analysisText);
                engine.process(jcas);
                resultMap = formatResults(jcas);
                pool.releaseJCas(jcas);
            } catch (Exception e) {
                e.printStackTrace();
                throw new ServletException(e);
            }
        }
        return resultMap;
    }

    private Map<String,List<String>>  formatResults(JCas jcas) throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        XmiCasSerializer.serialize(jcas.getCas(), output);
        String outputStr = output.toString();
        //Files.write(Paths.get("Result.xml"), outputStr.getBytes());
        XMLParser parser = new XMLParser();
        Map<String,List<String>> result = parser.parse(new ByteArrayInputStream(outputStr.getBytes()));
        
        
        
        ArrayList<LabMention> labMentions = new ArrayList<LabMention>(JCasUtil.select(jcas, LabMention.class)); 
        ArrayList<MedicationMention> medicationMentions = new ArrayList<MedicationMention>(JCasUtil.select(jcas, MedicationMention.class)); 

        List<String> labValueMentionList = new ArrayList();
        List<String> drugNerMentionList = new ArrayList();

        for (int i = 0; i < labMentions.size(); i++) {
                LabMention labMention = labMentions.get(i);
                JSONObject labMentionJsonObject = new JSONObject();
                if(labMention.getCoveredText() != null){
                        labMentionJsonObject.put("labName", labMention.getCoveredText());

                }
                if(labMention.getLabValue() != null && labMention.getLabValue().getArg2() != null && labMention.getLabValue().getArg2().getArgument()!= null && labMention.getLabValue().getArg2().getArgument().getCoveredText() != null){
                        String coveredText = labMention.getLabValue().getArg2().getArgument().getCoveredText();
                        List<String> coveredList = Arrays.asList(coveredText.split("/"));
                        labMentionJsonObject.put("value", coveredList);       
                } else {
                        labMentionJsonObject.put("value", new ArrayList()); 
                }

                final Collection<UmlsConcept> umlsConcepts = OntologyConceptUtil.getUmlsConcepts( labMention );
                for ( UmlsConcept umlsConcept : umlsConcepts ) {
                    final String cui = umlsConcept.getCui();
                    labMentionJsonObject.put("cui", cui);
                 }
                labValueMentionList.add(labMentionJsonObject.toString());
        }


        for (int i = 0; i < medicationMentions.size(); i ++) {
                MedicationMention medicationMention = medicationMentions.get(i);
                JSONObject medicationMentionJsonObject = new JSONObject();

                if(medicationMention.getCoveredText() != null){
                        medicationMentionJsonObject.put("name", medicationMention.getCoveredText());

                }
                
                if(medicationMention.getMedicationFrequency() != null){
                    medicationMentionJsonObject.put("frequency", medicationMention.getMedicationFrequency().getCategory());
                }

                if(medicationMention.getMedicationStrength() != null){
                    medicationMentionJsonObject.put("strength", medicationMention.getMedicationStrength().getCategory());
                }

                if(medicationMention.getMedicationAllergy() != null){
                    medicationMentionJsonObject.put("allergy", medicationMention.getMedicationAllergy().getCategory());
                }

                if(medicationMention.getMedicationDuration() != null){
                    medicationMentionJsonObject.put("duration", medicationMention.getMedicationDuration().getCategory());
                }

                if(medicationMention.getMedicationForm() != null){
                    medicationMentionJsonObject.put("form", medicationMention.getMedicationForm().getCategory());
                }

                if(medicationMention.getMedicationRoute() != null){
                    medicationMentionJsonObject.put("route", medicationMention.getMedicationRoute().getCategory());
                }

                if(medicationMention.getMedicationDosage() != null){
                    medicationMentionJsonObject.put("dosage", medicationMention.getMedicationDosage().getCategory());
                }

                if(medicationMention.getMedicationStrength() !=null){
                    medicationMentionJsonObject.put("strength", medicationMention.getMedicationStrength().getCategory());
                }

                if(medicationMention.getMedicationStatusChange()!=null){
                    medicationMentionJsonObject.put("statusChange", medicationMention.getMedicationStatusChange().getCategory());
                }

                final Collection<UmlsConcept> umlsConcepts = OntologyConceptUtil.getUmlsConcepts( medicationMention );
                for ( UmlsConcept umlsConcept : umlsConcepts ) {
                    final String cui = umlsConcept.getCui();
                    medicationMentionJsonObject.put("cui", cui);
                 }
                drugNerMentionList.add(medicationMentionJsonObject.toString());
        }
        
        result.put("LabValueMentionList", labValueMentionList);
        result.put("DrugNerMentionList", drugNerMentionList);
        
        return result;
    }
}
