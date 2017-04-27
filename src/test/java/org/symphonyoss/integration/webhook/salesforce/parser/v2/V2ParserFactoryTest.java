package org.symphonyoss.integration.webhook.salesforce.parser.v2;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.integration.model.message.MessageMLVersion;
import org.symphonyoss.integration.webhook.salesforce.parser.SalesforceParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Unit test for {@link V2ParserFactory}
 * Created by crepache on 25/04/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class V2ParserFactoryTest {

  @Spy
  private List<SalesforceParser> beans = new ArrayList<>();

  @InjectMocks
  private V2ParserFactory factory;

  @Before
  public void init() {
    factory.init();
  }

  @Test
  public void testNotAcceptable() {
    assertFalse(factory.accept(MessageMLVersion.V1));
  }

  @Test
  public void testAcceptable() {
    assertTrue(factory.accept(MessageMLVersion.V2));
  }


}