/**
 *    Copyright 2012 MegaFon
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package ru.histone.acceptance;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.util.StreamReaderDelegate;
import java.io.InputStreamReader;
import java.io.Reader;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.junit.ComparisonFailure;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.histone.HistoneTokensHolder;
import ru.histone.parser.Parser;
import ru.histone.parser.ParserException;
import ru.histone.tokenizer.TokenizerFactory;
import ru.histone.utils.StringEscapeUtils;

@RunWith(ParserAcceptanceTest.class)
public class ParserAcceptanceTest extends Runner {

	private Description testSuiteDescription;

	public ParserAcceptanceTest(Class<?> testClass) {
		testSuiteDescription = Description.createSuiteDescription("XML Test Cases");
	}

	@Override
	public Description getDescription() {
		return testSuiteDescription;
	}

	private static final Logger log = LoggerFactory.getLogger(ParserAcceptanceTest.class);

	private Gson gson;
    private Parser parser;

	@Override
	public void run(RunNotifier notifier) {
        gson = new Gson();

        TokenizerFactory tokenizerFactory = new TokenizerFactory(HistoneTokensHolder.getTokens());
        parser = new Parser(tokenizerFactory);

        Reader reader = new InputStreamReader(getClass().getResourceAsStream("/parser/cases.json") );

        for (JsonElement element : gson.fromJson(reader,JsonElement.class).getAsJsonArray()) {
            reader = new InputStreamReader(getClass().getResourceAsStream("/parser/" + element.getAsJsonPrimitive().getAsString()));
            runTestCasesFromXmlFile(notifier, reader);
        }
	}

    private void runTestCasesFromXmlFile(RunNotifier notifier, Reader reader) {
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        try {
			XMLStreamReader xmlStreamReader = inputFactory.createXMLStreamReader(reader);
			xmlStreamReader = new StreamReaderDelegate(xmlStreamReader) {
				public int next() throws XMLStreamException {
					while (true) {
						int event = super.next();
						switch (event) {
						case XMLStreamConstants.COMMENT:
						case XMLStreamConstants.PROCESSING_INSTRUCTION:
							continue;
						default:
							return event;
						}
					}
				}
			};

            while (xmlStreamReader.hasNext()) {
				int event = xmlStreamReader.next();

                String suiteName = null;
                if (event == XMLStreamConstants.START_ELEMENT) {
                    if ("suite".equals(xmlStreamReader.getLocalName())) {
                        suiteName = xmlStreamReader.getAttributeValue(null, "name");
                    } else if ("case".equals(xmlStreamReader.getLocalName())) {
                        readCase(notifier, xmlStreamReader);
                    }
                }else if(event == XMLStreamConstants.END_ELEMENT){
                    if ("suite".equals(xmlStreamReader.getLocalName())) {
                        suiteName = null;
                    }
                }
			}
		} catch (XMLStreamException e) {
			throw new RuntimeException("Error running XML test cases", e);
		} finally {

		}
    }

	private void readCase(RunNotifier notifier, XMLStreamReader xmlStreamReader) throws XMLStreamException {
		String input = null, expected = null;
		String[] exception = null;

		while (xmlStreamReader.hasNext()) {
			int event = xmlStreamReader.next();

			if (event == XMLStreamConstants.START_ELEMENT) {
				if ("input".equals(xmlStreamReader.getLocalName())) {
					input = readTagValue(xmlStreamReader);
				}
				if ("expected".equals(xmlStreamReader.getLocalName())) {
					expected = readTagValue(xmlStreamReader);
				}
				if ("exception".equals(xmlStreamReader.getLocalName())) {
					exception = readException(xmlStreamReader);
				}

			} else if (event == XMLStreamConstants.END_ELEMENT) {
				break;

			}

		}
		runTestCase(notifier, input, expected, exception);

	}

	private String[] readException(XMLStreamReader xmlStreamReader) throws XMLStreamException {
		String line = null, expected = null, found = null;

		while (xmlStreamReader.hasNext()) {
			int event = xmlStreamReader.next();

			if (event == XMLStreamConstants.START_ELEMENT) {
				if ("line".equals(xmlStreamReader.getLocalName())) {
					line = readTagValue(xmlStreamReader);
				}
				if ("expected".equals(xmlStreamReader.getLocalName())) {
					expected = readTagValue(xmlStreamReader);
				}
				if ("found".equals(xmlStreamReader.getLocalName())) {
					found = readTagValue(xmlStreamReader);
				}

			} else if (event == XMLStreamConstants.END_ELEMENT) {
				break;

			}

		}
		String[] exception = new String[] { line, expected, found };

		return exception;
	}

	private String readTagValue(XMLStreamReader xmlStreamReader) throws XMLStreamException {
		StringBuilder sb = new StringBuilder();

		while (xmlStreamReader.hasNext()) {
			int event = xmlStreamReader.next();

			if (event == XMLStreamConstants.END_ELEMENT) {
				break;
			} else if (event == XMLStreamConstants.CHARACTERS) {
				sb.append(xmlStreamReader.getText());
			}
		}

		return sb.toString();
	}

	long testIdx = 0;

	private void runTestCase(RunNotifier notifier, String input, String expected, String[] exception) {
		testIdx++;
		log.debug("case({}): input={}, expected={}, exception={}", new Object[] { testIdx, input, expected, exception });

		Description description = Description.createTestDescription(this.getClass(), testIdx + "_" + StringEscapeUtils.escapeJava(input));
		testSuiteDescription.addChild(description);
		notifier.fireTestStarted(description);

		try {
			JsonElement expectedJson = gson.fromJson(expected, JsonElement.class);
			JsonElement outputJson = null;

			if ((exception != null) && (exception.length > 0)) {
				int errLine = Integer.parseInt(exception[0]);
				String errExpectedToken = exception[1];
				String errFoundToken = exception[2];

				try {
					outputJson = parser.parse(input);
					log.debug("case({}): tree.json={}", new Object[] { testIdx, outputJson.toString() });
				} catch (ParserException e) {
					log.debug("case({}): e.message={}", new Object[] { testIdx, e.getMessage() });
					boolean matchOk = (e.getLineNumber() == errLine) && e.getExpected().equals(errExpectedToken) && e.getFound().equals(errFoundToken);

					if (matchOk) {
						notifier.fireTestFinished(description);
						return;
					} else {
						String msgF = "For input='" + input + "'";
						String expectedF = "line=" + errLine + ", expected=" + errExpectedToken + ", found=" + errFoundToken;
						String actualF = "line=" + e.getLineNumber() + ", expected=" + e.getExpected() + ", found=" + e.getFound();
						notifier.fireTestFailure(new Failure(description, new ComparisonFailure(msgF, expectedF, actualF)));
						return;
					}
				}

				String msgF = "For input='" + input + "'";
				String expectedF = "expected=" + errExpectedToken + ", found=" + errFoundToken;
				notifier.fireTestFailure(new Failure(description, new ComparisonFailure(msgF, expectedF, "")));
			} else {
				outputJson = parser.parse(input);
				log.debug("case({}): tree.json={}", new Object[] { testIdx, outputJson.toString() });

				boolean result = outputJson.equals(expectedJson);
				log.debug("case({}): result={}", new Object[] { testIdx, result });
				if (result) {
					notifier.fireTestFinished(description);
					return;
				}
                String msgF = "For input='" + input + "'";
                String actualF = outputJson.toString();
                notifier.fireTestFailure(new Failure(description, new ComparisonFailure(msgF, expectedJson.toString(), actualF)));
            }
		} catch (Throwable e) {
			String msg = "For input='" + input + "', expected=" + expected + ", but exception occured=" + e.getMessage();
			log.debug("Error msg={}", msg, e);
			notifier.fireTestFailure(new Failure(description, e));
		}
	}
}