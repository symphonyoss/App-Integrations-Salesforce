package org.symphonyoss.integration.webhook.salesforce.parser;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.integration.model.message.Message;
import org.symphonyoss.integration.webhook.WebHookPayload;
import org.symphonyoss.integration.webhook.exception.WebHookParseException;
import org.symphonyoss.integration.webhook.salesforce.SalesforceParseException;
import org.symphonyoss.integration.webhook.salesforce.parser
    .v2.OpportunityNotificationMetadataParser;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

@RunWith(MockitoJUnitRunner.class)
public class SalesforceWebHookParserAdapterTest {

  private static final String EVENT = "my_event";
  private static final String SAMPLE_BODY = "{\"foo\": \"bar\"}";
  private static final String MESSAGE = "parse me";

  @Mock
  private OpportunityNotificationMetadataParser parser;

  @InjectMocks
  SalesforceWebHookParserAdapter parserAdapter;

  private Message expectedMessage;

  @Before
  public void init() {
    expectedMessage = new Message();
    expectedMessage.setMessage(MESSAGE);
  }

  @Test
  public void testSupportedEvents() throws Exception {
    doReturn(Arrays.asList(EVENT)).when(parser).getEvents();

    List<String> events = parserAdapter.getEvents();
    assertNotNull(events);
    assertEquals(EVENT, events.get(0));
  }

  @Test
  public void testJsonParse() throws WebHookParseException {
    doReturn(expectedMessage).when(parser).parse(any(Map.class), any(JsonNode.class));

    Map<String, String> headers = new HashMap<>();
    headers.put("content-type", MediaType.APPLICATION_JSON);
    WebHookPayload payload = new WebHookPayload(Collections.EMPTY_MAP, headers, SAMPLE_BODY);

    assertEquals(expectedMessage, parserAdapter.parse(payload));
    verify(parser).parse(any(Map.class), any(JsonNode.class));
  }

  @Test(expected = SalesforceParseException.class)
  public void testJsonParseException() throws WebHookParseException {
    doReturn(expectedMessage).when(parser).parse(any(Map.class), any(JsonNode.class));

    Map<String, String> headers = new HashMap<>();
    headers.put("content-type", MediaType.APPLICATION_JSON);
    WebHookPayload payload = new WebHookPayload(Collections.EMPTY_MAP, headers, "invalid");

    assertEquals(expectedMessage, parserAdapter.parse(payload));
    verify(parser).parse(any(Map.class), any(JsonNode.class));
  }

  @Test
  public void testParse() throws Exception {
    doReturn(expectedMessage).when(parser).parse(any(WebHookPayload.class));

    WebHookPayload payload = new WebHookPayload(Collections.EMPTY_MAP, Collections.EMPTY_MAP, SAMPLE_BODY);

    assertEquals(expectedMessage, parserAdapter.parse(payload));
    verify(parser).parse(any(WebHookPayload.class));
  }

}