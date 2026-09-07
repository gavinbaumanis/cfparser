package cfml.parsing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import cfml.parsing.cfmentat.tag.CFMLTags;
import cfml.parsing.preferences.ParserPreferences;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.StartTag;

public class TestCFMLSourcePublicAPI {
	
	@Test
	public void testConstructorWithParserPreferences() {
		CFMLSource source = new CFMLSource("<cfset x=1>", new ParserPreferences());
		assertNotNull(source);
		assertFalse(source.getAllCFMLTags().isEmpty());
	}
	
	@Test
	public void testGetRowOnKnownPosition() {
		String contents = "line1\n<cfset x=1>\nline3";
		CFMLSource source = new CFMLSource(contents);
		int begin = contents.indexOf("<cfset");
		assertTrue(begin > 0);
		assertEquals(2, source.getRow(begin));
	}
	
	@Test
	public void testGetAllStartTagsAndElementsByType() {
		CFMLSource source = new CFMLSource("<cfset a=1><cfquery name=\"q\">x</cfquery>");
		List<StartTag> startTags = source.getAllStartTags();
		assertNotNull(startTags);
		assertFalse(startTags.isEmpty());
		
		List<Element> setElements = source.getAllElements(CFMLTags.CFML_SET);
		assertNotNull(setElements);
		assertFalse(setElements.isEmpty());
		assertEquals("cfset", setElements.get(0).getName());
	}
	
	@Test
	public void testOutputDocumentAndSourceFormatterNonNull() {
		CFMLSource source = new CFMLSource("<cfset x=1>");
		assertNotNull(source.getOutputDocument());
		assertNotNull(source.getSourceFormatter());
	}
	
	@Test
	public void testGetCacheDebugInfoNonNull() {
		CFMLSource source = new CFMLSource("<cfset x=1>");
		assertNotNull(source.getCacheDebugInfo());
	}
}
