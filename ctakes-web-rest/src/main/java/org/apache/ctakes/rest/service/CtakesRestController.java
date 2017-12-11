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
import java.util.List;
import java.util.Map;


/*
 * Rest web service that takes clinical text
 * as input and produces extracted text as output
 */
@RestController
public class CtakesRestController {

    private static final Logger LOGGER = Logger.getLogger(CtakesRestController.class);
    private static final String PIPER_FILE_PATH = "pipers/Default.piper";
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
    public Map<String,Map<String,List<String>>> getAnalyzedJSON(@RequestBody String analysisText)
            throws ServletException, IOException {
        Map<String,Map<String,List<String>>>  resultMap = null;
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

    private Map<String,Map<String,List<String>>>  formatResults(JCas jcas) throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        XmiCasSerializer.serialize(jcas.getCas(), output);
        String outputStr = output.toString();
        //Files.write(Paths.get("Result.xml"), outputStr.getBytes());
        XMLParser parser = new XMLParser();
        return parser.parse(new ByteArrayInputStream(outputStr.getBytes()));
    }
}
